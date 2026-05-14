package BLL;

import DAL.AmenityDAL;
import DAL.ReviewDAL;
import DAL.RoomDAL;
import DTO.AmenityDTO;
import DTO.RoomDTO;
import DTO.UserDTO;
import View.LoginFrame;
import View.LandlordMainFrame;
import View.RoomActionDialog;
import View.RoomCardPanel;
import View.RoomDetailFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller cho màn hình Landlord (chủ trọ).
 * Xử lý: load phòng của chủ trọ, thêm phòng mới, điều hướng.
 */
public class LandlordController {

    private final LandlordMainFrame view;
    private final UserDTO currentUser;
    private final RoomDAL roomDAL = new RoomDAL();
    private final AmenityDAL amenityDAL = new AmenityDAL();
    private final ReviewDAL reviewDAL = new ReviewDAL();

    public LandlordController(LandlordMainFrame view, UserDTO user) {
        this.view = view;
        this.currentUser = user;
        loadRooms();
        initEvents();
    }

    // ─────────────────────────────────────────────
    // Load danh sách phòng của landlord
    // ─────────────────────────────────────────────
    public void loadRooms() {
        JPanel pnRoomList = view.getPnRoomList();
        pnRoomList.removeAll();

        if (currentUser == null) { pnRoomList.revalidate(); return; }

        List<RoomDTO> rooms = roomDAL.getByLandlordId(currentUser.getUserId());

        for (RoomDTO room : rooms) {
            double avgRating = reviewDAL.getAverageRating(room.getRoomId());
            pnRoomList.add(buildLandlordCard(room, avgRating));
        }
        pnRoomList.revalidate();
        pnRoomList.repaint();
    }

    /** Tạo card ở chế độ Landlord (hiện nút quản lý). */
    private RoomCardPanel buildLandlordCard(RoomDTO room, double avgRating) {
        RoomCardPanel card = new RoomCardPanel();

        card.getLbTitle().setText(room.getTitle());
        card.getLbPrice().setText(String.format("%,.0f VNĐ/tháng", room.getPrice()));
        card.getLbArea().setText(room.getArea() + " m²");
        card.getLbAddress().setText(truncate(room.getAddress(), 40));
        card.getLbAvailability().setText(room.isAvailability() ? "Còn phòng" : "Hết phòng");
        card.getLbRating().setText(String.format("⭐ %.1f", avgRating));
        card.getLbStatus().setText(room.isStatus() ? "Đã duyệt" : "Chờ duyệt");

        // Đổ tiện nghi (tối đa 3)
        JPanel pnAmenity = card.getPnAmenity();
        pnAmenity.removeAll();
        List<AmenityDTO> amenities = room.getAmenityList();
        for (int i = 0; i < Math.min(3, amenities.size()); i++) {
            pnAmenity.add(new JLabel(amenities.get(i).getName()));
        }

        // Load ảnh
        loadThumbnail(card, room);

        // Click ảnh → mở RoomDetailFrame (chế độ chủ trọ)
        card.getLbThumb().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new RoomDetailFrame(room, currentUser).setVisible(true);
            }
        });

        // Nút Sửa → mở RoomActionDialog với dữ liệu phòng có sẵn
        card.getBtnUpdate().addActionListener(e -> {
            RoomActionDialog dlg = new RoomActionDialog(view, currentUser, room, this::loadRooms);
            dlg.setVisible(true);
        });

        // Nút Xóa
        card.getBtnDelete().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc muốn xóa phòng \"" + room.getTitle() + "\"?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (roomDAL.delete(room.getRoomId())) {
                    JOptionPane.showMessageDialog(view, "Đã xóa phòng.");
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Nút Đổi còn/hết
        card.getBtnAvailability().addActionListener(e -> {
            boolean newAvail = !room.isAvailability();
            if (roomDAL.updateAvailability(room.getRoomId(), newAvail)) {
                room.setAvailability(newAvail);
                card.getLbAvailability().setText(newAvail ? "Còn phòng" : "Hết phòng");
            }
        });

        return card;
    }

    // ─────────────────────────────────────────────
    // Events
    // ─────────────────────────────────────────────
    private void initEvents() {
        view.getBtnAddRoom().addActionListener(e -> {
            RoomActionDialog dlg = new RoomActionDialog(view, currentUser, null, this::loadRooms);
            dlg.setVisible(true);
        });

        view.getBtnLogout().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc muốn đăng xuất?", "Đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                view.dispose();
                new LoginFrame().setVisible(true);
            }
        });
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────
    private void loadThumbnail(RoomCardPanel card, RoomDTO room) {
        List<String> images = room.getImagePathList();
        if (images == null || images.isEmpty()) return;
        try {
            ImageIcon icon = new ImageIcon(images.get(0));
            Image scaled = icon.getImage().getScaledInstance(380, 200, Image.SCALE_SMOOTH);
            card.getLbThumb().setIcon(new ImageIcon(scaled));
            card.getLbThumb().setText("");
        } catch (Exception ignored) {}
    }

    private String truncate(String s, int max) {
        return (s == null || s.length() <= max) ? s : s.substring(0, max) + "…";
    }
}
