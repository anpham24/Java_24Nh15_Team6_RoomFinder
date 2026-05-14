package DAL;

import DTO.AccountDTO;
import Utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAL thao tác với bảng accounts.
 * Cung cấp các hàm CRUD và xác thực đăng nhập.
 */
public class AccountDAL {

    private static final Logger LOGGER = Logger.getLogger(AccountDAL.class.getName());

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────

    /**
     * Thêm tài khoản mới vào database.
     * @return true nếu thêm thành công.
     */
    public boolean insert(AccountDTO account) {
        String sql = "INSERT INTO accounts (username, password) VALUES (?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi thêm tài khoản: " + account.getUsername(), e);
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────

    /**
     * Lấy tất cả tài khoản.
     */
    public List<AccountDTO> getAll() {
        List<AccountDTO> list = new ArrayList<>();
        String sql = "SELECT username, password FROM accounts";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy danh sách tài khoản.", e);
        }
        return list;
    }

    /**
     * Tìm tài khoản theo username.
     */
    public AccountDTO getByUsername(String username) {
        String sql = "SELECT username, password FROM accounts WHERE username = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm tài khoản: " + username, e);
        }
        return null;
    }

    /**
     * Kiểm tra đăng nhập (so sánh username + password).
     * @return true nếu thông tin hợp lệ.
     */
    public boolean login(String username, String password) {
        String sql = "SELECT 1 FROM accounts WHERE username = ? AND password = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi đăng nhập.", e);
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────

    /**
     * Cập nhật mật khẩu của tài khoản.
     * @return true nếu cập nhật thành công.
     */
    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE accounts SET password = ? WHERE username = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật mật khẩu: " + username, e);
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────

    /**
     * Xóa tài khoản theo username (CASCADE sẽ xóa user liên quan).
     * @return true nếu xóa thành công.
     */
    public boolean delete(String username) {
        String sql = "DELETE FROM accounts WHERE username = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi xóa tài khoản: " + username, e);
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────

    private AccountDTO mapRow(ResultSet rs) throws SQLException {
        return new AccountDTO(
                rs.getString("username"),
                rs.getString("password")
        );
    }
}
