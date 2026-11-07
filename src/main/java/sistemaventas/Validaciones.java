package sistemaventas;

public final class Validaciones {
    private Validaciones() {}
    public static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        return valor.trim();
    }
}
