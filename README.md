# Sistema de Ventas e Inventario

Aplicación de escritorio desarrollada en **Java** para administrar las operaciones básicas de ventas e inventario. El sistema cuenta con una interfaz gráfica construida con **Java Swing** y organiza la información mediante clases que representan los elementos principales del negocio.

## Funcionalidades

- **Productos:** registrar y consultar productos.
- **Clientes:** registrar y consultar clientes.
- **Empleados:** registrar y consultar empleados.
- **Bodegas:** registrar y consultar bodegas.
- **Inventario:** agregar existencias y consultar el stock por producto y bodega.
- **Ventas:** crear órdenes, agregar productos a las órdenes, completar o cancelar órdenes y consultar su información.
- **Validación de datos:** comprueba campos obligatorios, formatos, cantidades y valores permitidos.
- **Persistencia local:** guarda y recupera la información mediante archivos serializados. El sistema crea una carpeta `datos` para almacenar los datos y utiliza un archivo de respaldo para ayudar a recuperar la información si el archivo principal presenta problemas.

## Tecnologías utilizadas

- Java
- Java Swing (`JFrame`, `JTabbedPane`, `JPanel`, `JButton`, `JTextArea` y `JOptionPane`)
- Maven para la administración del proyecto
- API de archivos de Java (`java.nio.file`)
- Serialización de objetos (`Serializable`, `ObjectInputStream` y `ObjectOutputStream`)

## Estructura del proyecto

Las clases principales del paquete `sistemaventas` son:

| Clase | Responsabilidad |
|---|---|
| `Main.java` | Punto de entrada; inicia la aplicación y muestra la ventana principal. |
| `VentanaPrincipal.java` | Construye la interfaz gráfica y conecta los botones con las operaciones. |
| `SistemaVentas.java` | Coordina los registros, consultas, inventario y órdenes de compra. |
| `Producto.java` | Representa los productos y sus datos. |
| `Cliente.java` | Representa a los clientes. |
| `Empleado.java` | Representa a los empleados. |
| `Bodega.java` | Representa las bodegas y su capacidad. |
| `Existencia.java` | Controla la cantidad de un producto en una bodega. |
| `OrdenCompra.java` | Representa una orden, sus productos, fecha, total y estado. |
| `EstadoSistema.java` | Mantiene las colecciones de productos, clientes, empleados, bodegas, existencias y órdenes. |
| `ArchivoService.java` | Carga y guarda el estado del sistema en archivos locales, con respaldo. |
| `Validaciones.java` | Contiene validaciones reutilizables para los datos ingresados. |

## Requisitos

- JDK instalado. El proyecto fue trabajado en un entorno con **JDK 21**.
- Apache NetBeans IDE.
- Maven, integrado en NetBeans o instalado en el equipo.

## Cómo ejecutar el proyecto

1. Descarga o clona este repositorio.
2. Abre Apache NetBeans.
3. Selecciona **File → Open Project**.
4. Busca y selecciona la carpeta del proyecto que contiene el archivo `pom.xml`.
5. Espera a que Maven resuelva las dependencias.
6. Ejecuta el proyecto desde NetBeans usando **Run Project**.

> Nota: para que el proyecto pueda abrirse como proyecto Maven, incluye en el repositorio el archivo `pom.xml` original junto con la carpeta `src`.

## Almacenamiento de datos

Al iniciar, el sistema intenta cargar la información guardada en la carpeta `datos`. El archivo principal es `sistema.dat`; también se utilizan archivos temporales y de respaldo durante el guardado.

La carpeta `datos` se genera al ejecutar la aplicación. Si contiene información real, revisa su contenido antes de publicarlo y no subas datos personales de clientes o empleados.


## Documentación

El repositorio incluye los documentos del proyecto en la carpeta `Documentacion/`:

- **[Informe del proyecto](Documentacion/Informe_del_Proyecto.docx):** presenta la descripción técnica, el paradigma de Programación Orientada a Objetos, las clases principales, sus relaciones, la arquitectura y la persistencia de datos.
- **[Manual de usuario](Documentacion/Manual_de_Usuario.docx):** explica cómo iniciar y utilizar el sistema, registrar datos, administrar existencias y gestionar órdenes de venta.

## Organización de los archivos

```text
SistemaVentasInventario/
├── README.md
├── .gitignore
├── pom.xml
├── src/
└── Documentacion/
    ├── Informe_del_Proyecto.docx
    └── Manual_de_Usuario.docx
```

## Autor

Proyecto académico: **Sistema de Ventas e Inventario**.

## Estado del proyecto

Proyecto académico en desarrollo.
