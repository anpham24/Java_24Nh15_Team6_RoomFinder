package BLL;

import DAL.UserDAL;
import DTO.UserDTO;
import View.AdminMainFrame;
import View.LoginFrame;
import View.RegisterFrame;
import View.TenantMainFrame;
import View.LandlordMainFrame;

import javax.swing.*;

/**
 * Controller xử lý logic màn hình Đăng nhập.
 */
public class LoginController {

    private final LoginFrame view;
    private final DAL.AccountDAL accountDAL = new DAL.AccountDAL();
    private final UserDAL userDAL = new UserDAL();

    public LoginController(LoginFrame view) {
        this.view = view;
        initEvents();
    }

    private void initEvents() {
        view.getBtnLogin().addActionListener(e -> handleLogin());
        view.getBtnNewAccount().addActionListener(e -> openRegister());

        // Cho phép nhấn Enter trong ô mật khẩu
        view.getTxtPassword().addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.getTxtUsername().getText().trim();
        String password = new String(view.getTxtPassword().getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Xác thực tài khoản
        boolean valid = accountDAL.login(username, password);
        if (!valid) {
            JOptionPane.showMessageDialog(view,
                    "Tên đăng nhập hoặc mật khẩu không đúng.",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            view.getTxtPassword().setText("");
            return;
        }

        // Lấy thông tin user để biết role
        UserDTO user = userDAL.getByUsername(username);
        if (user == null) {
            JOptionPane.showMessageDialog(view,
                    "Không tìm thấy thông tin người dùng.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Điều hướng theo role
        view.dispose();
        switch (user.getRole()) {
            case TENANT   -> new TenantMainFrame(user).setVisible(true);
            case LANDLORD -> new LandlordMainFrame(user).setVisible(true);
            case ADMIN    -> new AdminMainFrame(user).setVisible(true);
        }
    }

    private void openRegister() {
        view.setVisible(false);
        RegisterFrame rf = new RegisterFrame();
        rf.setVisible(true);
    }
}
