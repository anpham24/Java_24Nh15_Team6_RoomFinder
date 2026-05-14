package BLL;

import DAL.AmenityDAL;
import DAL.RoomDAL;
import DAL.UserDAL;
import DTO.AmenityDTO;
import DTO.RoomDTO;
import DTO.UserDTO;
import View.AdminMainFrame;
import View.LoginFrame;
import View.RoomDetailFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Controller cho màn hình Admin với 4 tab:
 * 1. Duyệt bài
 * 2. Quản lý bài đăng
 * 3. Quản lý người dùng
 * 4. Quản lý tiện nghi
 */
public class AdminController {

    private final AdminMainFrame view;
    private final UserDTO currentUser;
    private final RoomDAL    roomDAL    = new RoomDAL();
    private final UserDAL    userDAL    = new UserDAL();
    private final AmenityDAL amenityDAL = new AmenityDAL();

    public AdminController(AdminMainFrame view, UserDTO user) {
        this.view = view;
        this.currentUser = user;

        loadApproveTab();
        loadRoomManageTab(null, null);
        loadUserManageTab(null, null);
        loadAmenityTab();
        initEvents();
    }

    // ═══════════════════════════════════════════════════
    // TAB 1 – Duyệt bài
    // ═══════════════════════════════════════════════════
    private void loadApproveTab() {
        DefaultTableModel model = (DefaultTableModel) view.getTbApproveRoom().getModel();
        model.setRowCount(0);

        List<RoomDTO> pending = roomDAL.getByStatus(false); // status = false → PENDING
        for (RoomDTO r : pending) {
            UserDTO landlord = userDAL.getById(r.getLandlordId());
            String landlordName = landlord != null ? landlord.getName() : r.getLandlordId();
            model.addRow(new Object[]{
                r.getRoomId(), r.getTitle(), landlordName,
                r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate().toString() : "",
                "Chờ duyệt"
            });
        }
    }

    // ═══════════════════════════════════════════════════
    // TAB 2 – Quản lý bài đăng
    // ═══════════════════════════════════════════════════
    private void loadRoomManageTab(String keyword, Boolean statusFilter) {
        DefaultTableModel model = (DefaultTableModel) view.getTbRoomManage().getModel();
        model.setRowCount(0);

        List<RoomDTO> rooms = roomDAL.getAll();
        for (RoomDTO r : rooms) {
            // Lọc theo từ khóa
            if (keyword != null && !keyword.isEmpty()) {
                if (!r.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        && !r.getAddress().toLowerCase().contains(keyword.toLowerCase())) continue;
            }
            // Lọc theo status
            if (statusFilter != null && r.isStatus() != statusFilter) continue;

            UserDTO landlord = userDAL.getById(r.getLandlordId());
            String landlordName = landlord != null ? landlord.getName() : r.getLandlordId();
            model.addRow(new Object[]{
                r.getRoomId(), r.getTitle(), landlordName,
                String.format("%,.0f", r.getPrice()),
                r.isStatus() ? "Đã duyệt" : "Chờ duyệt"
            });
        }
    }

    // ═══════════════════════════════════════════════════
    // TAB 3 – Quản lý người dùng
    // ═══════════════════════════════════════════════════
    private void loadUserManageTab(String keyword, UserDTO.Role roleFilter) {
        DefaultTableModel model = (DefaultTableModel) view.getTbUserManage().getModel();
        model.setRowCount(0);

        List<UserDTO> users = userDAL.getAll();
        for (UserDTO u : users) {
            if (u.getRole() == UserDTO.Role.ADMIN) continue; // Không hiện admin

            if (keyword != null && !keyword.isEmpty()) {
                if (!u.getName().toLowerCase().contains(keyword.toLowerCase())
                        && !u.getUsername().toLowerCase().contains(keyword.toLowerCase())) continue;
            }
            if (roleFilter != null && u.getRole() != roleFilter) continue;

            model.addRow(new Object[]{
                u.getUserId(), u.getName(), u.getPhoneNumber(),
                u.getRole() == UserDTO.Role.LANDLORD ? "Chủ trọ" : "Người thuê",
                "Hoạt động"
            });
        }
    }

    // ═══════════════════════════════════════════════════
    // TAB 4 – Quản lý tiện nghi
    // ═══════════════════════════════════════════════════
    private void loadAmenityTab() {
        DefaultTableModel model = (DefaultTableModel) view.getTbAmenityManage().getModel();
        model.setRowCount(0);

        for (AmenityDTO a : amenityDAL.getAll()) {
            model.addRow(new Object[]{ a.getAmenityId(), a.getName() });
        }
    }

    // ═══════════════════════════════════════════════════
    // Events
    // ═══════════════════════════════════════════════════
    private void initEvents() {
        view.getBtnLogout().addActionListener(e -> handleLogout());

        // Tab 1
        view.getBtnRoomDetailTab1().addActionListener(e -> openRoomDetailFromTable(view.getTbApproveRoom()));
        view.getBtnApproveRoom().addActionListener(e -> handleApprove(true));
        view.getBtnDeclineRoom().addActionListener(e -> handleApprove(false));

        // Tab 2
        view.getBtnSearchRoom().addActionListener(e -> handleSearchRoom());
        view.getBtnRoomDetailTab2().addActionListener(e -> openRoomDetailFromTable(view.getTbRoomManage()));
        view.getBtnDeleteRoom().addActionListener(e -> handleDeleteRoom());

        // Tab 3
        view.getBtnSearchUser().addActionListener(e -> handleSearchUser());
        view.getBtnDeleteUser().addActionListener(e -> handleDeleteUser());

        // Tab 4
        view.getBtnAddAmenity().addActionListener(e -> handleAddAmenity());
        view.getBtnUpdateAmenity().addActionListener(e -> handleUpdateAmenity());
        view.getBtnDeleteAmenity().addActionListener(e -> handleDeleteAmenity());
    }

    // ─────────────────────────────────────────────
    // Tab 1 handlers
    // ─────────────────────────────────────────────
    private void handleApprove(boolean approve) {
        int row = view.getTbApproveRoom().getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(view, "Vui lòng chọn một dòng."); return; }

        String roomId = (String) view.getTbApproveRoom().getValueAt(row, 0);
        if (roomDAL.updateStatus(roomId, approve)) {
            JOptionPane.showMessageDialog(view, approve ? "Đã duyệt bài." : "Đã từ chối bài.");
            loadApproveTab();
            loadRoomManageTab(null, null);
        }
    }

    // ─────────────────────────────────────────────
    // Tab 2 handlers
    // ─────────────────────────────────────────────
    private void handleSearchRoom() {
        String keyword = view.getTxtSearchRoom().getText().trim();
        String statusStr = (String) view.getCboStatus().getSelectedItem();
        Boolean statusFilter = switch (statusStr) {
            case "Đã duyệt" -> true;
            case "Chờ duyệt" -> false;
            default -> null;
        };
        loadRoomManageTab(keyword, statusFilter);
    }

    private void handleDeleteRoom() {
        int row = view.getTbRoomManage().getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(view, "Vui lòng chọn một dòng."); return; }

        String roomId = (String) view.getTbRoomManage().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa bài đăng này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (roomDAL.delete(roomId)) {
                JOptionPane.showMessageDialog(view, "Đã xóa bài đăng.");
                loadRoomManageTab(null, null);
                loadApproveTab();
            }
        }
    }

    private void openRoomDetailFromTable(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(view, "Vui lòng chọn một dòng."); return; }

        String roomId = (String) table.getValueAt(row, 0);
        RoomDTO room = roomDAL.getById(roomId);
        if (room != null) {
            new RoomDetailFrame(room, currentUser).setVisible(true);
        }
    }

    // ─────────────────────────────────────────────
    // Tab 3 handlers
    // ─────────────────────────────────────────────
    private void handleSearchUser() {
        String keyword = view.getTxtSearchUser().getText().trim();
        String roleStr = (String) view.getCboRole().getSelectedItem();
        UserDTO.Role roleFilter = "Chủ trọ".equals(roleStr) ? UserDTO.Role.LANDLORD : UserDTO.Role.TENANT;
        loadUserManageTab(keyword, roleFilter);
    }

    private void handleDeleteUser() {
        int row = view.getTbUserManage().getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(view, "Vui lòng chọn một dòng."); return; }

        String userId = (String) view.getTbUserManage().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa người dùng này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            UserDTO user = userDAL.getById(userId);
            if (user != null && new DAL.AccountDAL().delete(user.getUsername())) {
                JOptionPane.showMessageDialog(view, "Đã xóa người dùng.");
                loadUserManageTab(null, null);
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─────────────────────────────────────────────
    // Tab 4 handlers
    // ─────────────────────────────────────────────
    private void handleAddAmenity() {
        String name = JOptionPane.showInputDialog(view, "Nhập tên tiện nghi mới:");
        if (name != null && !name.trim().isEmpty()) {
            AmenityDTO a = new AmenityDTO(java.util.UUID.randomUUID().toString(), name.trim());
            if (amenityDAL.insert(a)) {
                JOptionPane.showMessageDialog(view, "Đã thêm tiện nghi.");
                loadAmenityTab();
            }
        }
    }

    private void handleUpdateAmenity() {
        int row = view.getTbAmenityManage().getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(view, "Vui lòng chọn một dòng."); return; }

        String amenityId   = (String) view.getTbAmenityManage().getValueAt(row, 0);
        String currentName = (String) view.getTbAmenityManage().getValueAt(row, 1);
        String newName = JOptionPane.showInputDialog(view, "Sửa tên tiện nghi:", currentName);
        if (newName != null && !newName.trim().isEmpty()) {
            AmenityDTO a = new AmenityDTO(amenityId, newName.trim());
            if (amenityDAL.update(a)) {
                JOptionPane.showMessageDialog(view, "Đã cập nhật tiện nghi.");
                loadAmenityTab();
            }
        }
    }

    private void handleDeleteAmenity() {
        int row = view.getTbAmenityManage().getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(view, "Vui lòng chọn một dòng."); return; }

        String amenityId = (String) view.getTbAmenityManage().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa tiện nghi này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (amenityDAL.delete(amenityId)) {
                JOptionPane.showMessageDialog(view, "Đã xóa tiện nghi.");
                loadAmenityTab();
            }
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn đăng xuất?",
                "Đăng xuất", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            view.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
