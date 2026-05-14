import View.LoginFrame;
import java.awt.EventQueue;
import javax.swing.UIManager;

public class main {
    public static void main(String[] args) {
        setLookAndFeel();
        EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("Cannot set Nimbus look and feel: " + ex.getMessage());
        }
    }
}
