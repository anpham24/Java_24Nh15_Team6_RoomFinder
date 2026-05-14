package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class quản lý kết nối đến MySQL database.
 * Đảm bảo chỉ có một kết nối duy nhất trong toàn bộ ứng dụng.
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // --- Cấu hình kết nối ---
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "Room_Finder";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";          // Thay bằng mật khẩu MySQL của bạn

    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            HOST, PORT, DATABASE
    );

    // Instance duy nhất (volatile đảm bảo an toàn với multi-thread)
    private static volatile DatabaseConnection instance;
    private Connection connection;

    // Constructor private – ngăn khởi tạo từ bên ngoài
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            LOGGER.info("Kết nối database thành công.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Không tìm thấy MySQL JDBC Driver.", e);
            throw new RuntimeException("MySQL JDBC Driver không được tìm thấy.", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Kết nối database thất bại.", e);
            throw new RuntimeException("Không thể kết nối đến database.", e);
        }
    }

    /**
     * Trả về instance duy nhất của DatabaseConnection (Double-checked locking).
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Lấy đối tượng Connection để thực thi SQL.
     * Tự động kết nối lại nếu connection bị đóng hoặc không hợp lệ.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                LOGGER.warning("Connection không hợp lệ, đang kết nối lại...");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Không thể kết nối lại database.", e);
            throw new RuntimeException("Lỗi kết nối lại database.", e);
        }
        return connection;
    }

    /**
     * Đóng kết nối database (gọi khi tắt ứng dụng).
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                instance = null;
                LOGGER.info("Đã đóng kết nối database.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đóng kết nối.", e);
        }
    }
}
