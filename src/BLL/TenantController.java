package BLL;

import DAL.AmenityDAL;
import DAL.ReviewDAL;
import DAL.RoomDAL;
import DTO.AmenityDTO;
import DTO.RoomDTO;
import DTO.UserDTO;
import View.LoginFrame;
import View.RoomCardPanel;
import View.RoomDetailFrame;
import View.TenantMainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller cho màn hình Tenant (người thuê trọ).
 * Xử lý: load danh sách phòng, tìm kiếm, bộ lọc, đăng xuất.
 */
public class TenantController {

    private final TenantMainFrame view;
    private final UserDTO currentUser;
    private final RoomDAL roomDAL = new RoomDAL();
    private final AmenityDAL amenityDAL = new AmenityDAL();
    private final ReviewDAL reviewDAL = new ReviewDAL();

    // Danh sách checkbox tiện nghi được tạo động
    private List<JCheckBox> amenityCheckboxes = new ArrayList<>();

    public TenantController(TenantMainFrame view, UserDTO user) {
        this.view = view;
        this.currentUser = user;
        initAmenityCheckboxes();
        loadRooms(buildFilteredList());
        initEvents();
    }

    // ─────────────────────────────────────────────
    // Khởi tạo checkbox tiện nghi động
    // ─────────────────────────────────────────────
    private void initAmenityCheckboxes() {
        view.getPnAmenity().removeAll();
        amenityCheckboxes.clear();
        List<AmenityDTO> amenities = amenityDAL.getAll();
        for (AmenityDTO a : amenities) {
            JCheckBox cb = new JCheckBox(a.getName());
            cb.putClientProperty("amenityId", a.getAmenityId());
            amenityCheckboxes.add(cb);
            view.getPnAmenity().add(cb);
        }
        view.getPnAmenity().revalidate();
        view.getPnAmenity().repaint();
    }

    // ─────────────────────────────────────────────
    // Load / hiển thị danh sách phòng
    // ─────────────────────────────────────────────
    private void loadRooms(List<RoomDTO> rooms) {
        JPanel pnRoomList = view.getPnRoomList();
        pnRoomList.removeAll();

        for (RoomDTO room : rooms) {
            double avgRating = reviewDAL.getAverageRating(room.getRoomId());
            RoomCardPanel card = buildTenantCard(room, avgRating);
            pnRoomList.add(card);
        }
        pnRoomList.revalidate();
        pnRoomList.repaint();
    }

    /** Tạo card ở chế độ Tenant (ẩn nút quản lý). */
    private RoomCardPanel buildTenantCard(RoomDTO room, double avgRating) {
        RoomCardPanel card = new RoomCardPanel();

        card.getLbTitle().setText(room.getTitle());
        card.getLbPrice().setText(String.format("%,.0f VNĐ/tháng", room.getPrice()));
        card.getLbArea().setText(room.getArea() + " m²");
        card.getLbAddress().setText(truncate(room.getAddress(), 40));
        card.getLbAvailability().setText(room.isAvailability() ? "Còn phòng" : "Hết phòng");
        card.getLbRating().setText(String.format("⭐ %.1f", avgRating));

        // Ẩn nút quản lý (chế độ tenant)
        card.getBtnUpdate().setVisible(false);
        card.getBtnDelete().setVisible(false);
        card.getBtnAvailability().setVisible(false);
        card.getLbStatus().setVisible(false);

        // Đổ tiện nghi (tối đa 3 cái)
        JPanel pnAmenity = card.getPnAmenity();
        pnAmenity.removeAll();
        List<AmenityDTO> amenities = room.getAmenityList();
        for (int i = 0; i < Math.min(3, amenities.size()); i++) {
            pnAmenity.add(new JLabel(amenities.get(i).getName()));
        }

        // Load ảnh thumbnail
        loadThumbnail(card, room);

        // Click card → mở RoomDetailFrame
        card.getLbThumb().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new RoomDetailFrame(room, currentUser).setVisible(true);
            }
        });
        card.getLbTitle().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new RoomDetailFrame(room, currentUser).setVisible(true);
            }
        });

        return card;
    }

    // ─────────────────────────────────────────────
    // Gắn sự kiện
    // ─────────────────────────────────────────────
    private void initEvents() {
        view.getBtnSearch().addActionListener(e -> handleSearch());
        view.getBtnApply().addActionListener(e -> handleApply());
        view.getBtnLogout().addActionListener(e -> handleLogout());

        // Enter trong ô search
        view.getTxtSearch().addActionListener(e -> handleSearch());
    }

    /** Tìm kiếm đơn giản theo từ khóa. */
    private void handleSearch() {
        String keyword = view.getTxtSearch().getText().trim();
        List<RoomDTO> result = keyword.isEmpty()
                ? roomDAL.getAvailableApprovedRooms()
                : roomDAL.search(keyword);
        // Nạp tiện nghi cho từng room
        result.forEach(r -> r.setAmenityList(amenityDAL.getByRoomId(r.getRoomId())));
        loadRooms(result);
    }

    /** Áp dụng bộ lọc nâng cao (giá + tiện nghi + sắp xếp). */
    private void handleApply() {
        loadRooms(buildFilteredList());
    }

    /** Xây danh sách phòng theo toàn bộ điều kiện lọc. */
    private List<RoomDTO> buildFilteredList() {
        String keyword = view.getTxtSearch().getText().trim();
        String minStr  = view.getTxtMinPrice().getText().trim();
        String maxStr  = view.getTxtMaxPrice().getText().trim();

        double minPrice = parseDouble(minStr, 0);
        double maxPrice = parseDouble(maxStr, Double.MAX_VALUE);

        // Lấy amenityId được chọn
        List<String> selectedAmenityIds = new ArrayList<>();
        for (JCheckBox cb : amenityCheckboxes) {
            if (cb.isSelected()) {
                selectedAmenityIds.add((String) cb.getClientProperty("amenityId"));
            }
        }

        boolean sortByPrice  = view.getRdoPrice().isSelected();
        boolean sortByRating = view.getRdoReview().isSelected();

        List<RoomDTO> rooms = roomDAL.filterAdvanced(
                keyword, minPrice, maxPrice, selectedAmenityIds,
                sortByPrice, sortByRating
        );
        rooms.forEach(r -> r.setAmenityList(amenityDAL.getByRoomId(r.getRoomId())));
        return rooms;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn đăng xuất?", "Đăng xuất",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            view.dispose();
            new LoginFrame().setVisible(true);
        }
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

    private double parseDouble(String s, double fallback) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return fallback; }
    }
}
