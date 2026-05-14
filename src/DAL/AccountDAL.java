package DAL;

import DTOs.AccountDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAL {
    public AccountDTO findByUsername(String username) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return findByUsername(connection, username);
        }
    }

    public AccountDTO findByUsername(Connection connection, String username) throws SQLException {
        String sql = "SELECT username, password FROM accounts WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAccount(resultSet);
                }
            }
        }
        return null;
    }

    public boolean existsByUsername(String username) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return existsByUsername(connection, username);
        }
    }

    public boolean existsByUsername(Connection connection, String username) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean insert(AccountDTO account) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return insert(connection, account);
        }
    }

    public boolean insert(Connection connection, AccountDTO account) throws SQLException {
        String sql = "INSERT INTO accounts(username, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getPassword());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(String username, String password) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return updatePassword(connection, username, password);
        }
    }

    public boolean updatePassword(Connection connection, String username, String password) throws SQLException {
        String sql = "UPDATE accounts SET password = ? WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, password);
            statement.setString(2, username);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(String username) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return delete(connection, username);
        }
    }

    public boolean delete(Connection connection, String username) throws SQLException {
        String sql = "DELETE FROM accounts WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            return statement.executeUpdate() > 0;
        }
    }

    private AccountDTO mapAccount(ResultSet resultSet) throws SQLException {
        return new AccountDTO(
                resultSet.getString("username"),
                resultSet.getString("password"));
    }
}
