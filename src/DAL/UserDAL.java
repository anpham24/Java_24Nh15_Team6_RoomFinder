package DAL;

import DTO.UserDTO;
import DTO.UserDTO.Role;
import Utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAL {

    private static final Logger LOGGER = Logger.getLogger(UserDAL.class.getName());

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean insert(UserDTO user) {
        String sql = "INSERT INTO users (user_id, username, name, phone_number, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getName());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getRole().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi thêm user: " + user.getUsername(), e);
            return false;
        }
    }

    public List<UserDTO> getAll() {
        List<UserDTO> list = new ArrayList<>();
        String sql = "SELECT user_id, username, name, phone_number, role FROM users";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy danh sách users.", e);
        }
        return list;
    }

    public UserDTO getById(String userId) {
        String sql = "SELECT user_id, username, name, phone_number, role FROM users WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm user theo id: " + userId, e);
        }
        return null;
    }

    public UserDTO getByUsername(String username) {
        String sql = "SELECT user_id, username, name, phone_number, role FROM users WHERE username = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm user theo username: " + username, e);
        }
        return null;
    }

    public List<UserDTO> getByRole(Role role) {
        List<UserDTO> list = new ArrayList<>();
        String sql = "SELECT user_id, username, name, phone_number, role FROM users WHERE role = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy users theo role: " + role, e);
        }
        return list;
    }

    public boolean update(UserDTO user) {
        String sql = "UPDATE users SET name = ?, phone_number = ?, role = ? WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPhoneNumber());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật user: " + user.getUserId(), e);
            return false;
        }
    }

    public boolean delete(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi xóa user: " + userId, e);
            return false;
        }
    }

    private UserDTO mapRow(ResultSet rs) throws SQLException {
        return new UserDTO(
                rs.getString("user_id"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("phone_number"),
                Role.valueOf(rs.getString("role"))
        );
    }
}
