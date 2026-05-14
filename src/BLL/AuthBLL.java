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
            return LoginResultDTO.failure("Tên đăng nhập và mật khẩu là bắt buộc");
        }

        AccountDTO account = accountDAL.findByUsername(normalizedUsername);
        if (account == null || !account.getPassword().equals(password)) {
            return LoginResultDTO.failure("Tên đăng nhập hoặc mật khẩu không hợp lệ");
        }

        UserDTO user = userDAL.findByUsername(normalizedUsername);
        if (user == null) {
            return LoginResultDTO.failure("Tài khoản không có hồ sơ người dùng");
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
                    throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
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
            throw new IllegalArgumentException("Tên đăng nhập là bắt buộc");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu là bắt buộc");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Họ tên là bắt buộc");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Số điện thoại là bắt buộc");
        }
        if (!phoneNumber.trim().matches("[0-9+() .-]{8,20}")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }
        if (role == null || !role.canRegister()) {
            throw new IllegalArgumentException("Chỉ có tài khoản người thuê hoặc chủ trọ mới có thể đăng ký");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
