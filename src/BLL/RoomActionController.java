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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    /** Mở JFileChooser để chọn ảnh, copy vào folder Images, lưu đường dẫn local. */
    private void handleBrowseImage() {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif", "bmp"
        ));
        int result = fc.showOpenDialog(view);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File imagesDir = getImagesDir();
        List<String> errors = new ArrayList<>();

        for (File srcFile : fc.getSelectedFiles()) {
            try {
                String copiedPath = copyToImagesFolder(srcFile, imagesDir);
                if (!selectedImagePaths.contains(copiedPath)) {
                    selectedImagePaths.add(copiedPath);
                }
            } catch (IOException ex) {
                errors.add(srcFile.getName() + ": " + ex.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Không thể sao chép các ảnh sau:\n" + String.join("\n", errors),
                    "Lỗi sao chép ảnh", JOptionPane.WARNING_MESSAGE);
        }

        renderImagePreviews();
    }

    /**
     * Copy một file ảnh vào thư mục Images.<br>
     * Nếu tên file đã tồn tại, thêm UUID prefix để tránh ghi đè.<br>
     * @return đường dẫn tuyệt đối của file sau khi được copy vào Images.
     */
    private String copyToImagesFolder(File srcFile, File imagesDir) throws IOException {
        String fileName = srcFile.getName();
        File dest = new File(imagesDir, fileName);

        // Nếu tên trùng, thêm UUID prefix
        if (dest.exists() && !dest.getAbsolutePath().equals(srcFile.getAbsolutePath())) {
            String ext = fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf("."))
                    : "";
            String base = fileName.contains(".")
                    ? fileName.substring(0, fileName.lastIndexOf("."))
                    : fileName;
            fileName = base + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            dest = new File(imagesDir, fileName);
        }

        // Không copy nếu file nguồn đã ở đúng chỗ
        if (!srcFile.getAbsolutePath().equals(dest.getAbsolutePath())) {
            Files.copy(srcFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        // Trả về đường dẫn tương đối (tính từ thư mục gốc project) để lưu DB
        return "src/Images/" + dest.getName();
    }

    /**
     * Lấy thư mục Images, tự tạo nếu chưa tồn tại.<br>
     * Đường dẫn: [project_root]/src/Images
     */
    private File getImagesDir() {
        // Đường dẫn tương đối từ working directory (thường là project root khi chạy trong NetBeans)
        Path imagesPath = Paths.get("src", "Images");
        File imagesDir = imagesPath.toFile();
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        return imagesDir;
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
                    "PENDING",  // Phòng mới luôn ở trạng thái chờ duyệt
                    true,       // availability = còn phòng
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
