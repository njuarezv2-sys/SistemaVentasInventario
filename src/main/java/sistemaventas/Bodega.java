package sistemaventas;

import java.io.Serializable;

public class Bodega implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id, idEmpleado, direccion;
    private int capacidad;

    public Bodega(String id, String idEmpleado, int capacidad, String direccion) {
        setId(id); setIdEmpleado(idEmpleado); setCapacidad(capacidad); setDireccion(direccion);
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = Validaciones.texto(id, "ID bodega"); }
    public String getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = Validaciones.texto(idEmpleado, "Empleado responsable"); }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) {
        if (capacidad <= 0) throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        this.capacidad = capacidad;
    }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = Validaciones.texto(direccion, "Dirección"); }
}
