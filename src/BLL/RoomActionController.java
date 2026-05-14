package BLL;

import DAL.AmenityDAL;
import DAL.RoomDAL;
import DTO.AmenityDTO;
import DTO.RoomDTO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoomActionController {

    private final RoomDAL    roomDAL    = new RoomDAL();
    private final AmenityDAL amenityDAL = new AmenityDAL();

    public List<AmenityDTO> getAllAmenities() {
        return amenityDAL.getAll();
    }

    public List<String> copyImagesToProject(File[] files, List<String> errors) {
        File imagesDir = getImagesDir();
        List<String> paths = new ArrayList<>();
        for (File srcFile : files) {
            try {
                String path = copyToImagesFolder(srcFile, imagesDir);
                paths.add(path);
            } catch (IOException ex) {
                errors.add(srcFile.getName() + ": " + ex.getMessage());
            }
        }
        return paths;
    }

    public String saveRoom(String title, String address, String description,
                            String priceStr, String areaStr,
                            List<String> imagePaths, List<AmenityDTO> amenities,
                            String landlordId, String existingRoomId) {
        if (title.isEmpty() || address.isEmpty() || priceStr.isEmpty() || areaStr.isEmpty())
            return "Vui lòng điền đầy đủ: Tiêu đề, Địa chỉ, Giá tiền, Diện tích.";
        double price;
        int area;
        try {
            price = Double.parseDouble(priceStr);
            area  = Integer.parseInt(areaStr);
        } catch (NumberFormatException e) {
            return "Giá tiền và Diện tích phải là số hợp lệ.";
        }
        boolean success;
        if (existingRoomId == null) {
            RoomDTO newRoom = new RoomDTO(UUID.randomUUID().toString(), landlordId,
                    title, address, description, area, price, "PENDING", true, LocalDateTime.now());
            newRoom.setImagePathList(new ArrayList<>(imagePaths));
            newRoom.setAmenityList(amenities);
            success = roomDAL.insert(newRoom);
        } else {
            RoomDTO existing = roomDAL.getById(existingRoomId);
            if (existing == null) return "Không tìm thấy phòng cần cập nhật.";
            existing.setTitle(title);
            existing.setDescription(description);
            existing.setAddress(address);
            existing.setPrice(price);
            existing.setArea(area);
            existing.setImagePathList(new ArrayList<>(imagePaths));
            existing.setAmenityList(amenities);
            success = roomDAL.update(existing);
        }
        return success ? null : "Lưu thất bại. Vui lòng thử lại.";
    }

    private String copyToImagesFolder(File srcFile, File imagesDir) throws IOException {
        String fileName = srcFile.getName();
        File dest = new File(imagesDir, fileName);
        if (dest.exists() && !dest.getAbsolutePath().equals(srcFile.getAbsolutePath())) {
            String ext  = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
            String base = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
            fileName = base + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            dest = new File(imagesDir, fileName);
        }
        if (!srcFile.getAbsolutePath().equals(dest.getAbsolutePath()))
            Files.copy(srcFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return "src/Images/" + dest.getName();
    }

    private File getImagesDir() {
        Path imagesPath = Paths.get("src", "Images");
        File imagesDir = imagesPath.toFile();
        if (!imagesDir.exists()) imagesDir.mkdirs();
        return imagesDir;
    }
}
