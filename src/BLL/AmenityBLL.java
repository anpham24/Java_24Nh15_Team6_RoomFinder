package BLL;

import DAL.AmenityDAL;
import DTOs.AmenityDTO;
import DTOs.Role;
import java.sql.SQLException;
import java.util.List;

public class AmenityBLL {
    private final AmenityDAL amenityDAL = new AmenityDAL();

    public List<AmenityDTO> getAllAmenities() throws SQLException {
        return amenityDAL.findAll();
    }

    public AmenityDTO getAmenityById(int amenityId) throws SQLException {
        return amenityDAL.findById(amenityId);
    }

    public int addAmenity(String name) throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        String normalizedName = validateName(name);
        if (amenityDAL.existsByName(normalizedName)) {
            throw new IllegalArgumentException("Tiện nghi đã tồn tại");
        }
        return amenityDAL.insert(new AmenityDTO(0, normalizedName));
    }

    public boolean updateAmenity(AmenityDTO amenity) throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        if (amenity == null || amenity.getAmenityId() <= 0) {
            throw new IllegalArgumentException("Tiện nghi hợp lệ là bắt buộc");
        }

        String normalizedName = validateName(amenity.getName());
        AmenityDTO existing = amenityDAL.findById(amenity.getAmenityId());
        if (existing == null) {
            return false;
        }

        if (!existing.getName().equalsIgnoreCase(normalizedName) && amenityDAL.existsByName(normalizedName)) {
            throw new IllegalArgumentException("Tiện nghi đã tồn tại");
        }

        amenity.setName(normalizedName);
        return amenityDAL.update(amenity);
    }

    public boolean deleteAmenity(int amenityId) throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        if (amenityId <= 0) {
            throw new IllegalArgumentException("ID tiện nghi hợp lệ là bắt buộc");
        }
        return amenityDAL.delete(amenityId);
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên tiện nghi là bắt buộc");
        }
        return name.trim();
    }
}
