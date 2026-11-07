package sistemaventas;

import java.io.Serializable;

public class Empleado implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id, nombre, dpi, direccion, puesto;
    private double salario;

    public Empleado(String id, String nombre, String dpi, String direccion, String puesto, double salario) {
        setId(id); setNombre(nombre); setDpi(dpi); setDireccion(direccion); setPuesto(puesto); setSalario(salario);
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = Validaciones.texto(id, "ID empleado"); }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Validaciones.texto(nombre, "Nombre"); }
    public String getDpi() { return dpi; }
    public void setDpi(String dpi) {
        String v = Validaciones.texto(dpi, "DPI");
        if (!v.matches("\\d{13}")) throw new IllegalArgumentException("El DPI debe contener 13 dígitos.");
        this.dpi = v;
    }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = Validaciones.texto(direccion, "Dirección"); }
    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = Validaciones.texto(puesto, "Puesto"); }
    public double getSalario() { return salario; }
    public void setSalario(double salario) {
        if (!Double.isFinite(salario) || salario < 0) throw new IllegalArgumentException("El salario no puede ser negativo.");
        this.salario = Math.round(salario * 100.0) / 100.0;
    }
}
