import View.TenantMainFrame;

public class App {

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
        }
        java.awt.EventQueue.invokeLater(() -> new TenantMainFrame().setVisible(true));
    }
}
