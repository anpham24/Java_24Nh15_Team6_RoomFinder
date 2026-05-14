package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/Room_Finder"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Bangkok";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "0906";

    private DBConnection() {
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            // DriverManager can still load JDBC 4 drivers from the classpath.
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = readSetting("roomfinder.db.url", "ROOM_FINDER_DB_URL", DEFAULT_URL);
        String username = readSetting("roomfinder.db.username", "ROOM_FINDER_DB_USERNAME", DEFAULT_USERNAME);
        String password = readSetting("roomfinder.db.password", "ROOM_FINDER_DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, username, password);
    }

    private static String readSetting(String propertyName, String envName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value != null && !value.isBlank()) {
            return value;
        }

        value = System.getenv(envName);
        if (value != null && !value.isBlank()) {
            return value;
        }

        return defaultValue;
    }
}
