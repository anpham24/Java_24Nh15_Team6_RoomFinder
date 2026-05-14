package BLL;

import DAL.AccountDAL;
import DAL.UserDAL;
import DTO.UserDTO;

public class LoginController {

    private final AccountDAL accountDAL = new AccountDAL();
    private final UserDAL    userDAL    = new UserDAL();

    public UserDTO login(String username, String password) {
        if (!accountDAL.login(username, password)) return null;
        return userDAL.getByUsername(username);
    }
}
