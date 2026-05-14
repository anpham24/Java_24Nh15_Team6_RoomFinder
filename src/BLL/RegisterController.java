package BLL;

import DAL.AccountDAL;
import DAL.UserDAL;
import DTO.AccountDTO;
import DTO.UserDTO;
import java.util.UUID;

public class RegisterController {

    private final AccountDAL accountDAL = new AccountDAL();
    private final UserDAL    userDAL    = new UserDAL();

    public String register(String name, String phone, String username, String password, boolean isTenant) {
        if (name.isEmpty() || username.isEmpty() || password.isEmpty())
            return "Vui lòng nhập đầy đủ Tên, Tên đăng nhập và Mật khẩu.";
        if (password.length() < 6)
            return "Mật khẩu phải có ít nhất 6 ký tự.";
        if (accountDAL.getByUsername(username) != null)
            return "Tên đăng nhập \"" + username + "\" đã được sử dụng.";
        UserDTO.Role role = isTenant ? UserDTO.Role.TENANT : UserDTO.Role.LANDLORD;
        boolean ok = accountDAL.insert(new AccountDTO(username, password))
                  && userDAL.insert(new UserDTO(UUID.randomUUID().toString(), username, name, phone, role));
        return ok ? null : "Đăng ký thất bại. Vui lòng thử lại.";
    }
}
