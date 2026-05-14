package BLL;

import DAL.AmenityDAL;
import DAL.RoomDAL;
import DTO.AmenityDTO;
import DTO.RoomDTO;
import DTO.UserDTO;
import View.RoomActionDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controller cho Dialog Thêm / Sửa phòng.
 * Khi existingRoom == null → chế độ Thêm mới.
 * Khi existingRoom != null → chế độ Cập nhật.
 */
public class RoomActionController {

    private final RoomActionDialog view;
    private final UserDTO currentUser;
    private final RoomDTO existingRoom;
    private final Runnable onSaveCallback;

    private final RoomDAL    roomDAL    = new RoomDAL();
    private final AmenityDAL amenityDAL = new AmenityDAL();

    /** Danh sách đường dẫn ảnh người dùng đã chọn */
    private final List<String> selectedImagePaths = new ArrayList<>();

    /** Danh sách checkbox tiện nghi được tạo động */
    private final List<JCheckBox> amenityCheckboxes = new ArrayList<>();

    public RoomActionController(RoomActionDialog view, UserDTO user,
                                RoomDTO existing, Runnable onSave) {
        this.view           = view;
        this.currentUser    = user;
        this.existingRoom   = existing;
        this.onSaveCallback = onSave;

        initAmenityCheckboxes();
        if (existing != null) prefillForm();
        initEvents();
    }

    // ─────────────────────────────────────────────
    // Khởi tạo checkbox tiện nghi
    // ─────────────────────────────────────────────
    private void initAmenityCheckboxes() {
        JPanel pnAmenity = view.getPnAmenity();
        pnAmenity.removeAll();
        amenityCheckboxes.clear();

        List<AmenityDTO> all = amenityDAL.getAll();
        for (AmenityDTO a : all) {
            JCheckBox cb = new JCheckBox(a.getName());
            cb.putClientProperty("amenityId", a.getAmenityId());
            amenityCheckboxes.add(cb);
            pnAmenity.add(cb);
        }
        pnAmenity.revalidate();
        pnAmenity.repaint();
    }

    // ─────────────────────────────────────────────
    // Điền sẵn dữ liệu cũ (chế độ Sửa)
    // ─────────────────────────────────────────────
    private void prefillForm() {
        view.getTxtTitle().setText(existingRoom.getTitle());
        view.getTxtDescription().setText(existingRoom.getDescription());
        view.getTxtAddress().setText(existingRoom.getAddress());
        view.getTxtPrice().setText(String.valueOf((int) existingRoom.getPrice()));
        view.getTxtArea().setText(String.valueOf(existingRoom.getArea()));

        // Tick các tiện nghi đã có
        List<AmenityDTO> roomAmenities = existingRoom.getAmenityList();
        List<String> roomAmenityIds = new ArrayList<>();
        for (AmenityDTO a : roomAmenities) roomAmenityIds.add(a.getAmenityId());

        for (JCheckBox cb : amenityCheckboxes) {
            String id = (String) cb.getClientProperty("amenityId");
            cb.setSelected(roomAmenityIds.contains(id));
        }

        // Điền sẵn ảnh đã có
        selectedImagePaths.addAll(existingRoom.getImagePathList());
        renderImagePreviews();
    }

    // ─────────────────────────────────────────────
    // Events
    // ─────────────────────────────────────────────
    private void initEvents() {
        view.getBtnBrowse().addActionListener(e -> handleBrowseImage());
        view.getBtnSave().addActionListener(e -> handleSave());
        view.getBtnExit().addActionListener(e -> view.dispose());
    }

    /** Mở JFileChooser để chọn ảnh, thêm vào danh sách và hiển thị preview. */
    private void handleBrowseImage() {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif", "bmp"
        ));
        int result = fc.showOpenDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            for (File f : fc.getSelectedFiles()) {
                if (!selectedImagePaths.contains(f.getAbsolutePath())) {
                    selectedImagePaths.add(f.getAbsolutePath());
                }
            }
            renderImagePreviews();
        }
    }

    /** Hiển thị preview ảnh đã chọn vào pnImageList. */
    private void renderImagePreviews() {
        JPanel pnImageList = view.getPnImageList();
        pnImageList.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnImageList.removeAll();

        for (String path : selectedImagePaths) {
            JLabel thumb = new JLabel();
            try {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                thumb.setIcon(new ImageIcon(scaled));
            } catch (Exception ignored) {
                thumb.setText("[ảnh]");
            }

            // Nút xóa ảnh khi double-click
            final String p = path;
            thumb.setToolTipText("Double-click để xóa ảnh");
            thumb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        selectedImagePaths.remove(p);
                        renderImagePreviews();
                    }
                }
            });
            pnImageList.add(thumb);
        }
        pnImageList.revalidate();
        pnImageList.repaint();
    }

    // ─────────────────────────────────────────────
    // Lưu (Insert hoặc Update)
    // ─────────────────────────────────────────────
    private void handleSave() {
        // --- Validate ---
        String title = view.getTxtTitle().getText().trim();
        String desc  = view.getTxtDescription().getText().trim();
        String addr  = view.getTxtAddress().getText().trim();
        String priceStr = view.getTxtPrice().getText().trim();
        String areaStr  = view.getTxtArea().getText().trim();

        if (title.isEmpty() || addr.isEmpty() || priceStr.isEmpty() || areaStr.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng điền đầy đủ: Tiêu đề, Địa chỉ, Giá tiền, Diện tích.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        int area;
        try {
            price = Double.parseDouble(priceStr);
            area  = Integer.parseInt(areaStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Giá tiền và Diện tích phải là số hợp lệ.",
                    "Sai định dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Lấy tiện nghi được chọn ---
        List<AmenityDTO> selectedAmenities = new ArrayList<>();
        for (JCheckBox cb : amenityCheckboxes) {
            if (cb.isSelected()) {
                String id = (String) cb.getClientProperty("amenityId");
                selectedAmenities.add(new AmenityDTO(id, cb.getText()));
            }
        }

        boolean success;
        if (existingRoom == null) {
            // ----- INSERT -----
            RoomDTO newRoom = new RoomDTO(
                    UUID.randomUUID().toString(),
                    currentUser.getUserId(),
                    title, addr, desc, area, price,
                    false,  // status = PENDING
                    true,   // availability = còn phòng
                    java.time.LocalDateTime.now()
            );
            newRoom.setImagePathList(new ArrayList<>(selectedImagePaths));
            newRoom.setAmenityList(selectedAmenities);
            success = roomDAL.insert(newRoom);
        } else {
            // ----- UPDATE -----
            existingRoom.setTitle(title);
            existingRoom.setDescription(desc);
            existingRoom.setAddress(addr);
            existingRoom.setPrice(price);
            existingRoom.setArea(area);
            existingRoom.setImagePathList(new ArrayList<>(selectedImagePaths));
            existingRoom.setAmenityList(selectedAmenities);
            success = roomDAL.update(existingRoom);
        }

        if (success) {
            JOptionPane.showMessageDialog(view,
                    existingRoom == null ? "Thêm phòng thành công! Chờ Admin duyệt." : "Cập nhật thành công.");
            view.dispose();
            if (onSaveCallback != null) onSaveCallback.run();
        } else {
            JOptionPane.showMessageDialog(view, "Lưu thất bại. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
