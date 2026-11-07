package sistemaventas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EstadoSistema implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Producto> productos = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Empleado> empleados = new ArrayList<>();
    private final List<Bodega> bodegas = new ArrayList<>();
    private final List<Existencia> existencias = new ArrayList<>();
    private final List<OrdenCompra> ordenes = new ArrayList<>();

    public List<Producto> getProductos() { return productos; }
    public List<Cliente> getClientes() { return clientes; }
    public List<Empleado> getEmpleados() { return empleados; }
    public List<Bodega> getBodegas() { return bodegas; }
    public List<Existencia> getExistencias() { return existencias; }
    public List<OrdenCompra> getOrdenes() { return ordenes; }
}
