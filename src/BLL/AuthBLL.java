package BLL;

import DAL.AccountDAL;
import DAL.DBConnection;
import DAL.UserDAL;
import DTOs.AccountDTO;
import DTOs.LoginResultDTO;
import DTOs.Role;
import DTOs.UserDTO;
import java.sql.Connection;
import java.sql.SQLException;

public class AuthBLL {
    private final AccountDAL accountDAL = new AccountDAL();
    private final UserDAL userDAL = new UserDAL();

    public LoginResultDTO login(String username, String password) throws SQLException {
        String normalizedUsername = normalize(username);
        if (normalizedUsername.isBlank() || password == null || password.isBlank()) {
            return LoginResultDTO.failure("Username and password are required");
        }

        AccountDTO account = accountDAL.findByUsername(normalizedUsername);
        if (account == null || !account.getPassword().equals(password)) {
            return LoginResultDTO.failure("Invalid username or password");
        }

        UserDTO user = userDAL.findByUsername(normalizedUsername);
        if (user == null) {
            return LoginResultDTO.failure("Account has no user profile");
        }

        SessionContext.setCurrentUser(user);
        return LoginResultDTO.success(user);
    }

    public UserDTO register(String username, String password, String name, String phoneNumber, Role role)
            throws SQLException {
        String normalizedUsername = normalize(username);
        validateRegistration(normalizedUsername, password, name, phoneNumber, role);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (accountDAL.existsByUsername(connection, normalizedUsername)) {
                    throw new IllegalArgumentException("Username already exists");
                }

                AccountDTO account = new AccountDTO(normalizedUsername, password);
                accountDAL.insert(connection, account);

                UserDTO user = new UserDTO(0, normalizedUsername, name.trim(), phoneNumber.trim(), role);
                userDAL.insert(connection, user);
                connection.commit();
                return user;
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void logout() {
        SessionContext.clear();
    }

    private void validateRegistration(String username, String password, String name, String phoneNumber, Role role) {
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (!phoneNumber.trim().matches("[0-9+() .-]{8,20}")) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (role == null || !role.canRegister()) {
            throw new IllegalArgumentException("Only tenant or landlord accounts can be registered");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
