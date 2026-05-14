package BLL;

import DAL.AccountDAL;
import DAL.DBConnection;
import DAL.UserDAL;
import DTOs.Role;
import DTOs.UserDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserBLL {
    private final UserDAL userDAL = new UserDAL();
    private final AccountDAL accountDAL = new AccountDAL();

    public UserDTO getCurrentUser() {
        return SessionContext.requireLogin();
    }

    public UserDTO getUserById(int userId) throws SQLException {
        UserDTO currentUser = SessionContext.requireLogin();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getUserId() != userId) {
            throw new SecurityException("Permission denied");
        }
        return userDAL.findById(userId);
    }

    public List<UserDTO> getAllUsers() throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        return userDAL.findAll();
    }

    public List<UserDTO> searchUsers(String keyword, Role role) throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        return userDAL.search(keyword, role);
    }

    public boolean updateUser(UserDTO user) throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        validateUser(user);
        return userDAL.update(user);
    }

    public boolean deleteUser(int userId) throws SQLException {
        UserDTO currentUser = SessionContext.requireRole(Role.ADMIN);
        if (currentUser.getUserId() == userId) {
            throw new IllegalArgumentException("Current admin user cannot be deleted");
        }

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                UserDTO user = userDAL.findById(connection, userId);
                if (user == null) {
                    connection.rollback();
                    return false;
                }

                boolean deletedUser = userDAL.delete(connection, userId);
                accountDAL.delete(connection, user.getUsername());
                connection.commit();
                return deletedUser;
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void validateUser(UserDTO user) {
        if (user == null || user.getUserId() <= 0) {
            throw new IllegalArgumentException("Valid user is required");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }
}
