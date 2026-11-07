package sistemaventas;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrdenCompra implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id, dpiCliente, estado;
    private LocalDate fecha;
    private double total;
    private Map<String, Integer> productos;

    public OrdenCompra(String id, String dpiCliente) {
        this.id = Validaciones.texto(id, "ID orden");
        this.dpiCliente = Validaciones.texto(dpiCliente, "DPI cliente");
        this.fecha = LocalDate.now();
        this.estado = "PENDIENTE";
        this.total = 0.0;
        this.productos = new LinkedHashMap<>();
    }
    public String getId() { return id; }
    public String getDpiCliente() { return dpiCliente; }
    public String getEstado() { return estado; }
    public LocalDate getFecha() { return fecha; }
    public double getTotal() { return total; }
    public Map<String, Integer> getProductos() { return new LinkedHashMap<>(productos); }
    void agregarProducto(String codigo, int cantidad, double precio) {
        if (!"PENDIENTE".equals(estado)) throw new IllegalStateException("La orden ya fue procesada.");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser positiva.");
        productos.merge(codigo, cantidad, Math::addExact);
        total = Math.round((total + precio * cantidad) * 100.0) / 100.0;
    }
    void quitarProducto(String clave, int cantidad, double precio) {
        Integer actual = productos.get(clave);
        if (actual == null || cantidad <= 0 || cantidad > actual)
            throw new IllegalArgumentException("No se puede restaurar el detalle de la orden.");
        int nuevo = actual - cantidad;
        if (nuevo == 0) productos.remove(clave); else productos.put(clave, nuevo);
        total = Math.round((total - precio * cantidad) * 100.0) / 100.0;
        if (Math.abs(total) < 0.005) total = 0.0;
    }
    void restaurarPendiente() { estado = "PENDIENTE"; }
    void completar() {
        if (!"PENDIENTE".equals(estado)) throw new IllegalStateException("La orden ya fue procesada.");
        if (productos.isEmpty()) throw new IllegalStateException("La orden debe incluir al menos un producto.");
        estado = "COMPLETADA";
    }
    void cancelar() {
        if (!"PENDIENTE".equals(estado)) throw new IllegalStateException("La orden ya fue procesada.");
        if (productos.isEmpty()) throw new IllegalStateException("La orden no contiene productos.");
        estado = "CANCELADA";
    }
}
