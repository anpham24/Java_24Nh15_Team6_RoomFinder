package BLL;

import DAL.AccountDAL;
import DAL.UserDAL;
import DTO.AccountDTO;
import DTO.UserDTO;
import View.LoginFrame;
import View.RegisterFrame;

import javax.swing.*;
import java.util.UUID;

/**
 * Controller xử lý logic màn hình Đăng ký.
 */
public class RegisterController {

    private final RegisterFrame view;
    private final AccountDAL accountDAL = new AccountDAL();
    private final UserDAL userDAL = new UserDAL();

    public RegisterController(RegisterFrame view) {
        this.view = view;
        initEvents();
    }

    private void initEvents() {
        view.getBtnRegister().addActionListener(e -> handleRegister());
        view.getBtnExit().addActionListener(e -> goBackToLogin());
    }

    private void handleRegister() {
        String name     = view.getTxtName().getText().trim();
        String phone    = view.getTxtPhone().getText().trim();
        String username = view.getTxtUsername().getText().trim();
        String password = new String(view.getTxtPassword().getPassword());

        // --- Validation ---
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng nhập đầy đủ Tên, Tên đăng nhập và Mật khẩu.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(view,
                    "Mật khẩu phải có ít nhất 6 ký tự.",
                    "Mật khẩu yếu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!view.getRdoTenant().isSelected() && !view.getRdoLandlord().isSelected()) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn vai trò (Người tìm trọ hoặc Chủ trọ).",
                    "Chưa chọn vai trò", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- Kiểm tra username đã tồn tại ---
        if (accountDAL.getByUsername(username) != null) {
            JOptionPane.showMessageDialog(view,
                    "Tên đăng nhập \"" + username + "\" đã được sử dụng.",
                    "Trùng tên đăng nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDTO.Role role = view.getRdoTenant().isSelected()
                ? UserDTO.Role.TENANT
                : UserDTO.Role.LANDLORD;

        // --- Tạo account + user ---
        AccountDTO account = new AccountDTO(username, password);
        UserDTO user = new UserDTO(
                UUID.randomUUID().toString(), username, name, phone, role
        );

        boolean accOk  = accountDAL.insert(account);
        boolean userOk = accOk && userDAL.insert(user);

        if (userOk) {
            JOptionPane.showMessageDialog(view,
                    "Đăng ký thành công! Vui lòng đăng nhập.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            goBackToLogin();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Đăng ký thất bại. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackToLogin() {
        view.dispose();
        new LoginFrame().setVisible(true);
    }
}
