package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "Room_Finder";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "0906";

    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            HOST, PORT, DATABASE
    );

    private static volatile DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
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
