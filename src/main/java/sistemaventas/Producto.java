package sistemaventas;

import java.io.Serializable;

public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo, nombre, descripcion, marca;
    private double precio;

    public Producto(String codigo, String nombre, String descripcion, String marca, double precio) {
        setCodigo(codigo); setNombre(nombre); setDescripcion(descripcion); setMarca(marca); setPrecio(precio);
    }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = Validaciones.texto(codigo, "Código"); }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Validaciones.texto(nombre, "Nombre"); }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = Validaciones.texto(descripcion, "Descripción"); }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = Validaciones.texto(marca, "Marca"); }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) {
        if (!Double.isFinite(precio) || precio <= 0) throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        this.precio = Math.round(precio * 100.0) / 100.0;
    }
}
