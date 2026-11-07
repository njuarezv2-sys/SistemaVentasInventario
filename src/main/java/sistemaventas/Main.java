package sistemaventas;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup(); 
        SwingUtilities.invokeLater(() -> {
            try {
                SistemaVentas sistema = new SistemaVentas();
                new VentanaPrincipal(sistema).setVisible(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                    "No se pudo iniciar el sistema:\n" + ex.getMessage(),
                    "Error de datos", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
