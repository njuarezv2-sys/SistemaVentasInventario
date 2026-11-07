package sistemaventas;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Locale;

public class VentanaPrincipal extends JFrame {
    private final SistemaVentas sistema;
    private final JTextArea salida = new JTextArea();

    public VentanaPrincipal(SistemaVentas sistema) {
        this.sistema = sistema;
        setTitle("SISTEMA DE VENTAS E INVENTARIO");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(25, 55, 90));
tabs.setForeground(Color.WHITE);
tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.addTab("Productos", panel(
            boton("Registrar", this::registrarProducto),
            boton("Consultar", () -> mostrarProductos())));
        tabs.addTab("Clientes", panel(
            boton("Registrar", this::registrarCliente),
            boton("Consultar", () -> mostrarClientes())));
        tabs.addTab("Empleados", panel(
            boton("Registrar", this::registrarEmpleado),
            boton("Consultar", () -> mostrarEmpleados())));
        tabs.addTab("Bodegas", panel(
            boton("Registrar", this::registrarBodega),
            boton("Consultar", () -> mostrarBodegas())));
        tabs.addTab("Inventario", panel(
            boton("Agregar stock", this::agregarStock),
            boton("Consultar", () -> mostrarInventario())));
        tabs.addTab("Ventas", panel(
            boton("Crear orden", this::crearOrden),
            boton("Completar orden", () -> cambiarEstadoOrden(true)),
            boton("Cancelar orden", () -> cambiarEstadoOrden(false)),
            boton("Consultar", () -> mostrarOrdenes())));

        salida.setEditable(false);
        salida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        add(tabs, BorderLayout.NORTH);
        add(new JScrollPane(salida), BorderLayout.CENTER);
        salida.setText("Bienvenid@ a nuestro sistema.\nRegistra primero productos, clientes, empleados y bodegas.");
    }

    private interface Accion { void ejecutar() throws Exception; }
    private JButton boton(String texto, Accion accion) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> {
            try { accion.ejecutar(); }
            catch (Exception ex) { mensaje(ex.getMessage() == null ? "Ocurrió un error." : ex.getMessage()); }
        });
        return b;
    }
    private JPanel panel(JButton... botones) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        for (JButton b : botones) p.add(b);
        return p;
    }
    private void ejecutar(Runnable accion) {
        try { accion.run(); }
        catch (Exception ex) { mensaje(ex.getMessage() == null ? "Ocurrió un error." : ex.getMessage()); }
    }
    private String texto(String mensaje) {
        String v = JOptionPane.showInputDialog(this, mensaje);
        if (v == null) return null;
        if (v.trim().isEmpty()) throw new IllegalArgumentException("El campo es obligatorio.");
        return v.trim();
    }
    private Double dinero(String mensaje, boolean permitirCero) {
        String s = texto(mensaje);
        if (s == null) return null;
        try {
            double n = Double.parseDouble(s.replace(",", "."));
            if (!Double.isFinite(n) || n < 0 || (!permitirCero && n == 0))
                throw new NumberFormatException();
            return n;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Ingresa un importe válido.");
        }
    }
    private int entero(String mensaje) {
        String s = texto(mensaje);
        if (s == null) return -1;
        try {
            int n = Integer.parseInt(s);
            if (n <= 0) throw new NumberFormatException();
            return n;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Ingresa un entero mayor que cero.");
        }
    }
    private void mensaje(String s) { JOptionPane.showMessageDialog(this, s); }

    private void registrarProducto() {
        String codigo = texto("Código:");
        if (codigo == null) return;
        String nombre = texto("Nombre:");
        if (nombre == null) return;
        String desc = texto("Descripción:");
        if (desc == null) return;
        String marca = texto("Marca:");
        if (marca == null) return;
        Double precio = dinero("Precio en Q:", false);
        if (precio == null) return;
        try {
            sistema.registrarProducto(new Producto(codigo, nombre, desc, marca, precio));
            mensaje("Producto registrado.");
        } catch (Exception e) { throw new IllegalArgumentException(e.getMessage(), e); }
    }
    private void registrarCliente() throws Exception {
        String dpi = texto("DPI (13 dígitos):"); if (dpi == null) return;
        String nombre = texto("Nombre completo:"); if (nombre == null) return;
        String direccion = texto("Dirección:"); if (direccion == null) return;
        String fecha = texto("Fecha de nacimiento (AAAA-MM-DD):"); if (fecha == null) return;
        LocalDate nacimiento;
        try { nacimiento = LocalDate.parse(fecha); }
        catch (Exception e) { throw new IllegalArgumentException("Fecha inválida. Usa AAAA-MM-DD."); }
        String[] tipos = {"Regular", "VIP"};
        int t = JOptionPane.showOptionDialog(this, "Tipo de cliente", "Cliente",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
        if (t < 0) return;
        sistema.registrarCliente(new Cliente(dpi, nombre, direccion, nacimiento, tipos[t]));
        mensaje("Cliente registrado.");
    }
    private void registrarEmpleado() throws Exception {
        String id = texto("ID empleado:"); if (id == null) return;
        String nombre = texto("Nombre:"); if (nombre == null) return;
        String dpi = texto("DPI (13 dígitos):"); if (dpi == null) return;
        String direccion = texto("Dirección:"); if (direccion == null) return;
        String puesto = texto("Puesto:"); if (puesto == null) return;
        Double salario = dinero("Salario en Q:", true); if (salario == null) return;
        sistema.registrarEmpleado(new Empleado(id, nombre, dpi, direccion, puesto, salario));
        mensaje("Empleado registrado.");
    }
    private void registrarBodega() throws Exception {
        String id = texto("ID bodega:"); if (id == null) return;
        String emp = texto("ID del empleado responsable:"); if (emp == null) return;
        int capacidad = entero("Capacidad total en unidades:");
        if (capacidad < 0) return;
        String direccion = texto("Dirección:"); if (direccion == null) return;
        sistema.registrarBodega(new Bodega(id, emp, capacidad, direccion));
        mensaje("Bodega registrada.");
    }
    private void agregarStock() throws Exception {
        String codigo = texto("Código del producto:"); if (codigo == null) return;
        String bodega = texto("ID de bodega:"); if (bodega == null) return;
        int cantidad = entero("Cantidad a agregar:");
        if (cantidad < 0) return;
        sistema.agregarStock(codigo, bodega, cantidad);
        mensaje("Stock actualizado.");
    }
    private void crearOrden() throws Exception {
        String dpi = texto("DPI del cliente:"); if (dpi == null) return;
        OrdenCompra orden = sistema.crearOrden(dpi);
        while (true) {
            String codigo = JOptionPane.showInputDialog(this,
                "Orden " + orden.getId() + "\nCódigo del producto (Cancelar para terminar):");
            if (codigo == null) break;
            codigo = codigo.trim();
            if (codigo.isEmpty()) continue;
            String bodega = texto("ID de bodega desde donde se venderá:");
            if (bodega == null) break;
            int cantidad = entero("Cantidad:");
            if (cantidad < 0) break;
            sistema.agregarProductoOrden(orden.getId(), codigo, bodega, cantidad);
            mensaje("Producto agregado. Total actual: Q" + String.format(java.util.Locale.US, "%.2f", orden.getTotal()));
            String idActual = orden.getId();
            orden = sistema.getOrdenes().stream()
                .filter(o -> o.getId().equals(idActual)).findFirst().orElse(orden);
        }
        if (!orden.getProductos().isEmpty())
            mensaje("Orden creada: " + orden.getId() + "\nTotal: Q" + String.format(java.util.Locale.US, "%.2f", orden.getTotal())
                + "\nUsa Completar orden o Cancelar orden.");
    }
    private String seleccionarOrden() {
        if (sistema.getOrdenes().isEmpty()) { mensaje("No hay órdenes."); return null; }
        String[] ops = sistema.getOrdenes().stream()
            .map(o -> o.getId() + " | " + o.getEstado() + " | Q" + String.format(java.util.Locale.US, "%.2f", o.getTotal()))
            .toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Selecciona orden",
            "Órdenes", JOptionPane.QUESTION_MESSAGE, null, ops, ops[0]);
        if (sel == null) return null;
        return sel.split(" \\| ")[0];
    }
    private void cambiarEstadoOrden(boolean completar) {
        String id = seleccionarOrden(); if (id == null) return;
        try {
            if (completar) sistema.completarOrden(id);
            else sistema.cancelarOrden(id);
            mensaje(completar ? "Venta completada y stock actualizado." : "Orden cancelada sin descontar stock.");
        } catch (Exception e) { throw new IllegalArgumentException(e.getMessage(), e); }
    }

    private void mostrarProductos() {
        StringBuilder s = new StringBuilder("PRODUCTOS\n");
        sistema.getProductos().forEach(p -> s.append(p.getCodigo()).append(" | ")
            .append(p.getNombre()).append(" | ").append(p.getMarca()).append(" | Q")
            .append(String.format(java.util.Locale.US, "%.2f", p.getPrecio())).append("\n"));
        salida.setText(s.toString());
    }
    private void mostrarClientes() {
        StringBuilder s = new StringBuilder("CLIENTES\n");
        sistema.getClientes().forEach(c -> s.append(c.getDpi()).append(" | ")
            .append(c.getNombre()).append(" | ").append(c.getTipo()).append("\n"));
        salida.setText(s.toString());
    }
    private void mostrarEmpleados() {
        StringBuilder s = new StringBuilder("EMPLEADOS\n");
        sistema.getEmpleados().forEach(e -> s.append(e.getId()).append(" | ")
            .append(e.getNombre()).append(" | ").append(e.getPuesto()).append("\n"));
        salida.setText(s.toString());
    }
    private void mostrarBodegas() {
        StringBuilder s = new StringBuilder("BODEGAS\n");
        sistema.getBodegas().forEach(b -> s.append(b.getId()).append(" | empleado ")
            .append(b.getIdEmpleado()).append(" | capacidad ").append(b.getCapacidad())
            .append(" | ").append(b.getDireccion()).append("\n"));
        salida.setText(s.toString());
    }
    private void mostrarInventario() {
        StringBuilder s = new StringBuilder("INVENTARIO\n");
        sistema.getExistencias().forEach(x -> s.append(x.getCodigoProducto())
            .append(" | Bodega ").append(x.getIdBodega()).append(" | Stock ")
            .append(x.getCantidad()).append("\n"));
        salida.setText(s.toString());
    }
    private void mostrarOrdenes() {
        StringBuilder s = new StringBuilder("ÓRDENES\n");
        sistema.getOrdenes().forEach(o -> s.append(o.getId()).append(" | ")
            .append(o.getFecha()).append(" | cliente ").append(o.getDpiCliente())
            .append(" | Q").append(String.format(java.util.Locale.US, "%.2f", o.getTotal())).append(" | ").append(o.getEstado()).append("\n"));
        salida.setText(s.toString());
    }
}
