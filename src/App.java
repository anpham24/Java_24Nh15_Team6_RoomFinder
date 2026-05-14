import View.LoginFrame;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║          ROOM FINDER – Điểm khởi động chính          ║
 * ║                                                      ║
 * ║  Chạy class này để start ứng dụng.                   ║
 * ║  Mỗi View có main() riêng là do Netbeans tự sinh ra  ║
 * ║  để preview form trong IDE – KHÔNG dùng để chạy app. ║
 * ╚══════════════════════════════════════════════════════╝
 *
 * Luồng điều hướng:
 *   App.main()
 *     └─► LoginFrame  (+ LoginController tự gắn sự kiện)
 *           ├─► TenantMainFrame   (TENANT)
 *           ├─► LandlordMainFrame (LANDLORD)
 *           └─► AdminMainFrame    (ADMIN)
 */
public class App {

    public static void main(String[] args) {
        // Đặt Look & Feel hệ thống (giao diện native của Windows/macOS/Linux)
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            // Fallback: dùng L&F mặc định của Java nếu lỗi
        }

        // Toàn bộ Swing phải chạy trên Event Dispatch Thread (EDT)
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
