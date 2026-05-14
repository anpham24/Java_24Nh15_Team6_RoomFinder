package BLL;

import DAL.AmenityDAL;
import DAL.ReviewDAL;
import DAL.RoomDAL;
import DAL.UserDAL;
import DTO.AmenityDTO;
import DTO.ReviewDTO;
import DTO.RoomDTO;
import DTO.UserDTO;
import View.RoomActionDialog;
import View.RoomDetailFrame;
import View.ReviewPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Controller cho màn hình Chi tiết phòng.
 * Hỗ trợ cả 3 role: TENANT, LANDLORD, ADMIN.
 */
public class RoomDetailController {

    private final RoomDetailFrame view;
    private RoomDTO room;
    private final UserDTO currentUser;
    private final RoomDAL    roomDAL    = new RoomDAL();
    private final ReviewDAL  reviewDAL  = new ReviewDAL();
    private final AmenityDAL amenityDAL = new AmenityDAL();
    private final UserDAL    userDAL    = new UserDAL();

    public RoomDetailController(RoomDetailFrame view, RoomDTO room, UserDTO user) {
        this.view = view;
        this.room = room;
        this.currentUser = user;

        if (room == null) return;

        populateRoomInfo();
        loadImages();
        loadAmenities();
        loadReviews();
        applyRoleVisibility();
        initEvents();
    }

    // ─────────────────────────────────────────────
    // Đổ thông tin phòng lên View
    // ─────────────────────────────────────────────
    private void populateRoomInfo() {
        view.getLbTitle().setText(room.getTitle());
        view.getLbAvailability().setText(room.isAvailability() ? "Còn phòng" : "Hết phòng");
        view.getLbStatus().setText(statusToLabel(room.getStatus()));
        view.getLbDescription().setText(
                "<html><body style='width:430px'>" + room.getDescription() + "</body></html>"
        );
        view.getLbAddress().setText(room.getAddress());
        view.getLbPrice().setText(String.format("%,.0f VNĐ/tháng", room.getPrice()));
        view.getLbArea().setText(room.getArea() + " m²");

        // Rating
        double avg = reviewDAL.getAverageRating(room.getRoomId());
        view.getLbRating().setText(String.format("⭐ %.1f", avg));

        // Số điện thoại chủ trọ
        UserDTO landlord = userDAL.getById(room.getLandlordId());
        view.getLbPhone().setText(landlord != null ? landlord.getPhoneNumber() : "");
    }

    // ─────────────────────────────────────────────
    // Ảnh
    // ─────────────────────────────────────────────
    private void loadImages() {
        List<String> paths = room.getImagePathList();
        if (paths == null || paths.isEmpty()) return;

        // Ảnh chính
        setMainImage(paths.get(0));

        // Ảnh con
        JPanel pnImageList = view.getPnImageList();
        pnImageList.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnImageList.removeAll();

        for (String path : paths) {
            JLabel thumb = new JLabel();
            try {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                thumb.setIcon(new ImageIcon(scaled));
            } catch (Exception ignored) {
                thumb.setText("[ảnh]");
            }
            thumb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            thumb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    setMainImage(path);
                }
            });
            pnImageList.add(thumb);
        }
        pnImageList.revalidate();
        pnImageList.repaint();
    }

    private void setMainImage(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image scaled = icon.getImage().getScaledInstance(550, 560, Image.SCALE_SMOOTH);
            view.getLbMainImage().setIcon(new ImageIcon(scaled));
            view.getLbMainImage().setText("");
        } catch (Exception ignored) {}
    }

    // ─────────────────────────────────────────────
    // Tiện nghi
    // ─────────────────────────────────────────────
    private void loadAmenities() {
        JPanel pnAmenity = view.getPnAmenity();
        pnAmenity.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnAmenity.removeAll();
        for (AmenityDTO a : room.getAmenityList()) {
            pnAmenity.add(new JLabel("✓ " + a.getName()));
        }
        pnAmenity.revalidate();
        pnAmenity.repaint();
    }

    // ─────────────────────────────────────────────
    // Reviews
    // ─────────────────────────────────────────────
    private void loadReviews() {
        JPanel pnReviewList = view.getPnReviewList();
        pnReviewList.setLayout(new BoxLayout(pnReviewList, BoxLayout.Y_AXIS));
        pnReviewList.removeAll();

        List<ReviewDTO> reviews = reviewDAL.getByRoomId(room.getRoomId());
        for (ReviewDTO rv : reviews) {
            ReviewPanel panel = new ReviewPanel();
            UserDTO tenant = userDAL.getById(rv.getTenantId());
            panel.getLbName().setText(tenant != null ? tenant.getName() : rv.getTenantId());
            panel.getLbContent().setText(
                    "<html><body style='width:450px'>" + rv.getComment() + "</body></html>"
            );
            panel.getLbRating().setText("⭐ " + rv.getRating() + "/5");
            pnReviewList.add(panel);
        }
        pnReviewList.revalidate();
        pnReviewList.repaint();
    }

    // ─────────────────────────────────────────────
    // Phân quyền hiển thị theo role
    // ─────────────────────────────────────────────
    private void applyRoleVisibility() {
        if (currentUser == null) return;

        switch (currentUser.getRole()) {
            case TENANT -> {
                // Tenant: hiện review, ẩn quản lý
                view.getBtnUpdate().setVisible(false);
                view.getBtnDelete().setVisible(false);
                view.getBtnAvailability().setVisible(false);
                view.getLbStatus().setVisible(false);
                view.getTxtReview().setVisible(true);
                view.getCboRating().setVisible(true);
                view.getBtnSubmit().setVisible(true);
                view.getLbPhone().setVisible(true);
            }
            case LANDLORD -> {
                // Landlord: hiện quản lý, ẩn review
                boolean isOwner = room.getLandlordId().equals(currentUser.getUserId());
                view.getBtnUpdate().setVisible(isOwner);
                view.getBtnDelete().setVisible(isOwner);
                view.getBtnAvailability().setVisible(isOwner);
                view.getLbStatus().setVisible(true);
                view.getTxtReview().setVisible(false);
                view.getCboRating().setVisible(false);
                view.getLbStar().setVisible(false);
                view.getBtnSubmit().setVisible(false);
            }
            case ADMIN -> {
                // Admin: chỉ xóa, ẩn review
                view.getBtnUpdate().setVisible(false);
                view.getBtnDelete().setVisible(true);
                view.getBtnAvailability().setVisible(false);
                view.getLbStatus().setVisible(true);
                view.getTxtReview().setVisible(false);
                view.getCboRating().setVisible(false);
                view.getLbStar().setVisible(false);
                view.getBtnSubmit().setVisible(false);
            }
        }
    }

    // ─────────────────────────────────────────────
    // Events
    // ─────────────────────────────────────────────
    private void initEvents() {
        // Đăng review (chỉ Tenant)
        view.getBtnSubmit().addActionListener(e -> handleSubmitReview());

        // Sửa bài (Landlord)
        view.getBtnUpdate().addActionListener(e -> {
            RoomActionDialog dlg = new RoomActionDialog(
                    (Frame) SwingUtilities.getWindowAncestor(view),
                    currentUser, room,
                    () -> {
                        // Reload lại room sau khi lưu
                        this.room = roomDAL.getById(room.getRoomId());
                        populateRoomInfo();
                        loadImages();
                        loadAmenities();
                    }
            );
            dlg.setVisible(true);
        });

        // Xóa bài (Landlord + Admin)
        view.getBtnDelete().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc muốn xóa phòng này?", "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (roomDAL.delete(room.getRoomId())) {
                    JOptionPane.showMessageDialog(view, "Đã xóa phòng.");
                    view.dispose();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Đổi còn/hết (Landlord)
        view.getBtnAvailability().addActionListener(e -> {
            boolean newAvail = !room.isAvailability();
            if (roomDAL.updateAvailability(room.getRoomId(), newAvail)) {
                room.setAvailability(newAvail);
                view.getLbAvailability().setText(newAvail ? "Còn phòng" : "Hết phòng");
            }
        });
    }

    private void handleSubmitReview() {
        String content = view.getTxtReview().getText().trim();
        if (content.isEmpty() || content.equals("<Nhập đánh giá>")) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập nội dung đánh giá.");
            return;
        }
        int rating;
        try { rating = Integer.parseInt((String) view.getCboRating().getSelectedItem()); }
        catch (Exception ex) { rating = 5; }

        ReviewDTO review = new ReviewDTO(
                UUID.randomUUID().toString(),
                room.getRoomId(),
                currentUser.getUserId(),
                rating, content,
                java.time.LocalDateTime.now()
        );

        if (reviewDAL.insert(review)) {
            JOptionPane.showMessageDialog(view, "Đánh giá thành công!");
            view.getTxtReview().setText("");
            loadReviews();
            // Cập nhật rating trung bình
            double avg = reviewDAL.getAverageRating(room.getRoomId());
            view.getLbRating().setText(String.format("⭐ %.1f", avg));
        } else {
            JOptionPane.showMessageDialog(view, "Đăng đánh giá thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String statusToLabel(String status) {
        if (status == null) return "Chờ duyệt";
        return switch (status) {
            case "APPROVED" -> "✔ Đã duyệt";
            case "DECLINED" -> "✘ Bị từ chối";
            default         -> "⏳ Chờ duyệt";
        };
    }
}
