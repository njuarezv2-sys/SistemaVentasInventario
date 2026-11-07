package sistemaventas;

import java.io.Serializable;
import java.time.LocalDate;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dpi, nombre, direccion, tipo;
    private LocalDate fechaNacimiento;

    public Cliente(String dpi, String nombre, String direccion, LocalDate fechaNacimiento, String tipo) {
        setDpi(dpi); setNombre(nombre); setDireccion(direccion); setFechaNacimiento(fechaNacimiento); setTipo(tipo);
    }
    public String getDpi() { return dpi; }
    public void setDpi(String dpi) {
        String v = Validaciones.texto(dpi, "DPI");
        if (!v.matches("\\d{13}")) throw new IllegalArgumentException("El DPI debe contener 13 dígitos.");
        this.dpi = v;
    }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Validaciones.texto(nombre, "Nombre"); }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = Validaciones.texto(direccion, "Dirección"); }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de nacimiento no puede estar en el futuro.");
        this.fechaNacimiento = fechaNacimiento;
    }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) {
        String v = Validaciones.texto(tipo, "Tipo").toUpperCase();
        if (!v.equals("REGULAR") && !v.equals("VIP")) throw new IllegalArgumentException("El tipo debe ser Regular o VIP.");
        this.tipo = v.equals("VIP") ? "VIP" : "Regular";
    }
}
