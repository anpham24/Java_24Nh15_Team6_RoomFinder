package DAL;

import DTO.AmenityDTO;
import Utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmenityDAL {

    private static final Logger LOGGER = Logger.getLogger(AmenityDAL.class.getName());

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean insert(AmenityDTO amenity) {
        String sql = "INSERT INTO amenities (amenity_id, name) VALUES (?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, amenity.getAmenityId());
            ps.setString(2, amenity.getName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi thêm amenity: " + amenity.getName(), e);
            return false;
        }
    }

    public List<AmenityDTO> getAll() {
        List<AmenityDTO> list = new ArrayList<>();
        String sql = "SELECT amenity_id, name FROM amenities ORDER BY name";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy danh sách amenities.", e);
        }
        return list;
    }

    public AmenityDTO getById(String amenityId) {
        String sql = "SELECT amenity_id, name FROM amenities WHERE amenity_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, amenityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm amenity: " + amenityId, e);
        }
        return null;
    }

    public List<AmenityDTO> getByRoomId(String roomId) {
        List<AmenityDTO> list = new ArrayList<>();
        String sql = "SELECT a.amenity_id, a.name FROM amenities a "
                   + "JOIN room_amenities ra ON a.amenity_id = ra.amenity_id "
                   + "WHERE ra.room_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy amenities của phòng: " + roomId, e);
        }
        return list;
    }

    public boolean update(AmenityDTO amenity) {
        String sql = "UPDATE amenities SET name = ? WHERE amenity_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, amenity.getName());
            ps.setString(2, amenity.getAmenityId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật amenity: " + amenity.getAmenityId(), e);
            return false;
        }
    }

    public boolean delete(String amenityId) {
        String sql = "DELETE FROM amenities WHERE amenity_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, amenityId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi xóa amenity: " + amenityId, e);
            return false;
        }
    }

    private AmenityDTO mapRow(ResultSet rs) throws SQLException {
        return new AmenityDTO(
                rs.getString("amenity_id"),
                rs.getString("name")
        );
    }
}
