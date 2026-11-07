package sistemaventas;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ArchivoService {
    private final Path carpeta;
    private final Path archivo;
    private final Path temporal;
    private final Path respaldo;

    public ArchivoService() {
        carpeta = Paths.get("datos");
        archivo = carpeta.resolve("sistema.dat");
        temporal = carpeta.resolve("sistema.tmp");
        respaldo = carpeta.resolve("sistema.bak");
    }

    public EstadoSistema cargar() throws IOException {
        Files.createDirectories(carpeta);
        if (Files.exists(archivo)) {
            try {
                return leerObjeto(archivo);
            } catch (Exception ex) {
                if (Files.exists(respaldo)) {
                    try {
                        EstadoSistema recuperado = leerObjeto(respaldo);
                        guardar(recuperado);
                        return recuperado;
                    } catch (Exception ex2) {
                        throw new IOException("El archivo principal y el respaldo están dañados.", ex2);
                    }
                }
                throw new IOException("No se pudo leer el archivo de datos.", ex);
            }
        }
        if (Files.exists(respaldo)) {
            try {
                return leerObjeto(respaldo);
            } catch (Exception ex) {
                throw new IOException("El respaldo también está dañado.", ex);
            }
        }
        return new EstadoSistema();
    }

    private EstadoSistema leerObjeto(Path ruta) throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(ruta))) {
            Object objeto = entrada.readObject();
            if (!(objeto instanceof EstadoSistema))
                throw new IOException("Formato de archivo desconocido.");
            return (EstadoSistema) objeto;
        }
    }

    public void guardar(EstadoSistema estado) throws IOException {
        Files.createDirectories(carpeta);
        try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(
                temporal, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            salida.writeObject(estado);
            salida.flush();
        }

        // Comprueba que el archivo temporal se pueda volver a leer antes de sustituir el principal.
        try {
            leerObjeto(temporal);
        } catch (ClassNotFoundException ex) {
            throw new IOException("No se pudo verificar el archivo temporal.", ex);
        }

        if (Files.exists(archivo)) {
            Files.copy(archivo, respaldo, StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
