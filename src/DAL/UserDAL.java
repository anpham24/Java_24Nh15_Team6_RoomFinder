package DAL;

import DTOs.Role;
import DTOs.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAL {
    public UserDTO findById(int userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return findById(connection, userId);
        }
    }

    public UserDTO findById(Connection connection, int userId) throws SQLException {
        String sql = "SELECT user_id, username, name, phone_number, role FROM users WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }
        return null;
    }

    public UserDTO findByUsername(String username) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return findByUsername(connection, username);
        }
    }

    public UserDTO findByUsername(Connection connection, String username) throws SQLException {
        String sql = "SELECT user_id, username, name, phone_number, role FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }
        return null;
    }

    public List<UserDTO> findAll() throws SQLException {
        String sql = "SELECT user_id, username, name, phone_number, role FROM users ORDER BY user_id";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            return mapUsers(resultSet);
        }
    }

    public List<UserDTO> search(String keyword, Role role) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT user_id, username, name, phone_number, role FROM users WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(username) LIKE ? OR LOWER(name) LIKE ? OR phone_number LIKE ?)");
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (role != null) {
            sql.append(" AND role = ?");
            params.add(role.toDbValue());
        }

        sql.append(" ORDER BY user_id");

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            bindParams(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapUsers(resultSet);
            }
        }
    }

    public int insert(UserDTO user) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return insert(connection, user);
        }
    }

    public int insert(Connection connection, UserDTO user) throws SQLException {
        String sql = "INSERT INTO users(username, name, phone_number, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getName());
            statement.setString(3, user.getPhoneNumber());
            statement.setString(4, user.getRole().toDbValue());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    user.setUserId(id);
                    return id;
                }
            }
        }
        return 0;
    }

    public boolean update(UserDTO user) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return update(connection, user);
        }
    }

    public boolean update(Connection connection, UserDTO user) throws SQLException {
        String sql = "UPDATE users SET name = ?, phone_number = ?, role = ? WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPhoneNumber());
            statement.setString(3, user.getRole().toDbValue());
            statement.setInt(4, user.getUserId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return delete(connection, userId);
        }
    }

    public boolean delete(Connection connection, int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        }
    }

    private void bindParams(PreparedStatement statement, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            statement.setObject(i + 1, params.get(i));
        }
    }

    private List<UserDTO> mapUsers(ResultSet resultSet) throws SQLException {
        List<UserDTO> users = new ArrayList<>();
        while (resultSet.next()) {
            users.add(mapUser(resultSet));
        }
        return users;
    }

    private UserDTO mapUser(ResultSet resultSet) throws SQLException {
        return new UserDTO(
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("name"),
                resultSet.getString("phone_number"),
                Role.fromDbValue(resultSet.getString("role")));
    }
}
