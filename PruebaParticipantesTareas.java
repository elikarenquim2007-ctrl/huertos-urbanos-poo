import javax.swing.*;

/** Clase para ejecutar SOLO este módulo y ver cómo se ve (clic derecho > Run). */
public class PruebaParticipantesTareas {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Participantes y Tareas - Huertos urbanos");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(new VentanaParticipantesTareas());
            ventana.setSize(760, 520);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
