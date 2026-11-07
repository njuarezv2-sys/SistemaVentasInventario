package sistemaventas;

import java.io.Serializable;

public class Existencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigoProducto, idBodega;
    private int cantidad;

    public Existencia(String codigoProducto, String idBodega, int cantidad) {
        this.codigoProducto = Validaciones.texto(codigoProducto, "Código producto");
        this.idBodega = Validaciones.texto(idBodega, "ID bodega");
        setCantidad(cantidad);
    }
    public String getCodigoProducto() { return codigoProducto; }
    public String getIdBodega() { return idBodega; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        this.cantidad = cantidad;
    }
    public void agregar(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a agregar debe ser positiva.");
        this.cantidad = Math.addExact(this.cantidad, cantidad);
    }
    public void descontar(int cantidad) {
        if (cantidad <= 0 || cantidad > this.cantidad) throw new IllegalArgumentException("Stock insuficiente.");
        this.cantidad -= cantidad;
    }
}
