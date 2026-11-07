package sistemaventas;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class SistemaVentas {
    private final ArchivoService archivos = new ArchivoService();
    private EstadoSistema estado;

    public SistemaVentas() throws IOException {
        estado = archivos.cargar();
        validarEstado();
    }

    public List<Producto> getProductos() { return Collections.unmodifiableList(estado.getProductos()); }
    public List<Cliente> getClientes() { return Collections.unmodifiableList(estado.getClientes()); }
    public List<Empleado> getEmpleados() { return Collections.unmodifiableList(estado.getEmpleados()); }
    public List<Bodega> getBodegas() { return Collections.unmodifiableList(estado.getBodegas()); }
    public List<Existencia> getExistencias() { return Collections.unmodifiableList(estado.getExistencias()); }
    public List<OrdenCompra> getOrdenes() { return Collections.unmodifiableList(estado.getOrdenes()); }

    private void persistir() throws IOException { archivos.guardar(estado); }

    private void validarEstado() throws IOException {
        Set<String> codigos = new HashSet<>();
        for (Producto p : estado.getProductos())
            if (!codigos.add(p.getCodigo().toUpperCase()))
                throw new IOException("Hay códigos de producto duplicados en los datos.");
        Set<String> dpis = new HashSet<>();
        for (Cliente c : estado.getClientes())
            if (!dpis.add(c.getDpi()))
                throw new IOException("Hay DPI de cliente duplicados en los datos.");
        Set<String> idsEmp = new HashSet<>();
        for (Empleado e : estado.getEmpleados())
            if (!idsEmp.add(e.getId().toUpperCase()))
                throw new IOException("Hay IDs de empleado duplicados en los datos.");
        Set<String> idsBod = new HashSet<>();
        for (Bodega b : estado.getBodegas()) {
            if (!idsBod.add(b.getId().toUpperCase()))
                throw new IOException("Hay IDs de bodega duplicados en los datos.");
            if (buscarEmpleado(b.getIdEmpleado()) == null)
                throw new IOException("Una bodega hace referencia a un empleado inexistente.");
        }
        Set<String> clavesExistencia = new HashSet<>();
        for (Existencia x : estado.getExistencias()) {
            if (buscarProducto(x.getCodigoProducto()) == null || buscarBodega(x.getIdBodega()) == null)
                throw new IOException("Hay existencias con producto o bodega inexistente.");
            String clave = x.getCodigoProducto().toUpperCase() + "|" + x.getIdBodega().toUpperCase();
            if (!clavesExistencia.add(clave)) throw new IOException("Hay existencias duplicadas.");
        }
        for (OrdenCompra o : estado.getOrdenes()) {
            if (buscarCliente(o.getDpiCliente()) == null)
                throw new IOException("Una orden hace referencia a un cliente inexistente.");
            for (String clave : o.getProductos().keySet()) {
                String[] partes;
                try { partes = separarClave(clave); }
                catch (RuntimeException ex) { throw new IOException("Hay un detalle de orden inválido.", ex); }
                if (buscarProducto(partes[0]) == null || buscarBodega(partes[1]) == null)
                    throw new IOException("Una orden hace referencia a un producto o bodega inexistente.");
            }
        }
    }

    private Producto buscarProducto(String codigo) {
        for (Producto p : estado.getProductos()) if (p.getCodigo().equalsIgnoreCase(codigo)) return p;
        return null;
    }
    private Cliente buscarCliente(String dpi) {
        for (Cliente c : estado.getClientes()) if (c.getDpi().equals(dpi)) return c;
        return null;
    }
    private Empleado buscarEmpleado(String id) {
        for (Empleado e : estado.getEmpleados()) if (e.getId().equalsIgnoreCase(id)) return e;
        return null;
    }
    private Bodega buscarBodega(String id) {
        for (Bodega b : estado.getBodegas()) if (b.getId().equalsIgnoreCase(id)) return b;
        return null;
    }
    private Existencia buscarExistencia(String codigo, String bodega) {
        for (Existencia x : estado.getExistencias())
            if (x.getCodigoProducto().equalsIgnoreCase(codigo) &&
                x.getIdBodega().equalsIgnoreCase(bodega)) return x;
        return null;
    }

    public void registrarProducto(Producto p) throws IOException {
        if (buscarProducto(p.getCodigo()) != null) throw new IllegalArgumentException("El código de producto ya existe.");
        estado.getProductos().add(p);
        try { persistir(); } catch (IOException ex) { estado.getProductos().remove(p); throw ex; }
    }
    public void registrarCliente(Cliente c) throws IOException {
        if (buscarCliente(c.getDpi()) != null) throw new IllegalArgumentException("El DPI del cliente ya existe.");
        estado.getClientes().add(c);
        try { persistir(); } catch (IOException ex) { estado.getClientes().remove(c); throw ex; }
    }
    public void registrarEmpleado(Empleado e) throws IOException {
        if (buscarEmpleado(e.getId()) != null) throw new IllegalArgumentException("El ID de empleado ya existe.");
        for (Empleado otro : estado.getEmpleados())
            if (otro.getDpi().equals(e.getDpi())) throw new IllegalArgumentException("El DPI ya pertenece a otro empleado.");
        estado.getEmpleados().add(e);
        try { persistir(); } catch (IOException ex) { estado.getEmpleados().remove(e); throw ex; }
    }
    public void registrarBodega(Bodega b) throws IOException {
        if (buscarBodega(b.getId()) != null) throw new IllegalArgumentException("El ID de bodega ya existe.");
        if (buscarEmpleado(b.getIdEmpleado()) == null) throw new IllegalArgumentException("Registra primero al empleado responsable.");
        estado.getBodegas().add(b);
        try { persistir(); } catch (IOException ex) { estado.getBodegas().remove(b); throw ex; }
    }

    public void agregarStock(String codigo, String idBodega, int cantidad) throws IOException {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        if (buscarProducto(codigo) == null) throw new IllegalArgumentException("Producto inexistente.");
        Bodega b = buscarBodega(idBodega);
        if (b == null) throw new IllegalArgumentException("Bodega inexistente.");
        Existencia x = buscarExistencia(codigo, idBodega);
        int ocupacion = 0;
        for (Existencia ex : estado.getExistencias())
            if (ex.getIdBodega().equalsIgnoreCase(idBodega)) ocupacion = Math.addExact(ocupacion, ex.getCantidad());
        if (Math.addExact(ocupacion, cantidad) > b.getCapacidad())
            throw new IllegalArgumentException("La cantidad supera la capacidad disponible de la bodega.");
        boolean nueva = (x == null);
        int cantidadAnterior = nueva ? 0 : x.getCantidad();
        if (nueva) {
            x = new Existencia(codigo, idBodega, cantidad);
            estado.getExistencias().add(x);
        } else {
            x.agregar(cantidad);
        }
        try { persistir(); } catch (IOException ex) {
            if (nueva) estado.getExistencias().remove(x);
            else x.setCantidad(cantidadAnterior);
            throw ex;
        }
    }

    public OrdenCompra crearOrden(String dpiCliente) throws IOException {
        if (buscarCliente(dpiCliente) == null) throw new IllegalArgumentException("Cliente inexistente.");
        String id = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        OrdenCompra o = new OrdenCompra(id, dpiCliente);
        estado.getOrdenes().add(o);
        try { persistir(); } catch (IOException ex) { estado.getOrdenes().remove(o); throw ex; }
        return o;
    }

    public void agregarProductoOrden(String idOrden, String codigo, String idBodega, int cantidad) throws IOException {
        OrdenCompra o = buscarOrden(idOrden);
        if (o == null) throw new IllegalArgumentException("Orden inexistente.");
        if (!"PENDIENTE".equals(o.getEstado())) throw new IllegalStateException("La orden ya fue procesada.");
        Producto p = buscarProducto(codigo);
        if (p == null) throw new IllegalArgumentException("Producto inexistente.");
        Existencia x = buscarExistencia(codigo, idBodega);
        if (x == null || cantidad <= 0 || x.getCantidad() < cantidad)
            throw new IllegalArgumentException("No hay suficiente stock en esa bodega.");
        String clave = claveDetalle(codigo, idBodega);
        int anterior = o.getProductos().getOrDefault(clave, 0);
        if (anterior + cantidad > x.getCantidad())
            throw new IllegalArgumentException("La cantidad total de la orden supera el stock de esa bodega.");
        o.agregarProducto(clave, cantidad, p.getPrecio());
        try { persistir(); } catch (IOException ex) {
            o.quitarProducto(clave, cantidad, p.getPrecio());
            throw ex;
        }
    }

    private OrdenCompra buscarOrden(String id) {
        for (OrdenCompra o : estado.getOrdenes()) if (o.getId().equals(id)) return o;
        return null;
    }

    public void completarOrden(String idOrden) throws IOException {
        OrdenCompra o = buscarOrden(idOrden);
        if (o == null) throw new IllegalArgumentException("Orden inexistente.");
        if (!"PENDIENTE".equals(o.getEstado())) throw new IllegalStateException("La orden ya fue procesada.");
        if (o.getProductos().isEmpty()) throw new IllegalStateException("La orden debe tener al menos un producto.");

        Map<Existencia, Integer> descuentos = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> item : o.getProductos().entrySet()) {
            String[] partes = separarClave(item.getKey());
            Existencia x = buscarExistencia(partes[0], partes[1]);
            if (x == null || x.getCantidad() < item.getValue())
                throw new IllegalArgumentException("Stock insuficiente para " + partes[0] + " en bodega " + partes[1] + ".");
            descuentos.put(x, item.getValue());
        }

        Map<Existencia, Integer> anteriores = new LinkedHashMap<>();
        for (Map.Entry<Existencia, Integer> d : descuentos.entrySet()) {
            anteriores.put(d.getKey(), d.getKey().getCantidad());
            d.getKey().descontar(d.getValue());
        }
        o.completar();
        try {
            persistir();
        } catch (IOException ex) {
            for (Map.Entry<Existencia, Integer> a : anteriores.entrySet()) a.getKey().setCantidad(a.getValue());
            o.restaurarPendiente();
            throw ex;
        }
    }

    public void cancelarOrden(String idOrden) throws IOException {
        OrdenCompra o = buscarOrden(idOrden);
        if (o == null) throw new IllegalArgumentException("Orden inexistente.");
        o.cancelar();
        try { persistir(); } catch (IOException ex) {
            o.restaurarPendiente();
            throw ex;
        }
    }

    private String claveDetalle(String codigo, String bodega) { return codigo + "@@" + bodega; }
    private String[] separarClave(String clave) {
        int pos = clave.indexOf("@@");
        if (pos <= 0 || pos + 2 >= clave.length()) throw new IllegalStateException("Detalle de orden inválido.");
        return new String[]{clave.substring(0, pos), clave.substring(pos + 2)};
    }
}
