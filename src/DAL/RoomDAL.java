package DAL;

import DTO.AmenityDTO;
import DTO.RoomDTO;
import Utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAL thao tác với bảng rooms, room_images và room_amenities.
 *
 * <p>Các thao tác ghi (insert / update / delete) trên một Room thường
 * cần cập nhật đồng thời nhiều bảng → dùng transaction để đảm bảo
 * tính toàn vẹn dữ liệu.</p>
 */
public class RoomDAL {

    private static final Logger LOGGER = Logger.getLogger(RoomDAL.class.getName());

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────

    /**
     * Thêm phòng mới kèm ảnh và tiện nghi (trong một transaction).
     * @return true nếu thành công.
     */
    public boolean insert(RoomDTO room) {
        Connection conn = getConn();
        String sqlRoom = "INSERT INTO rooms (room_id, landlord_id, title, address, description, "
                       + "area, price, status, availability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn.setAutoCommit(false);

            // 1. Thêm phòng
            try (PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setString(1, room.getRoomId());
                ps.setString(2, room.getLandlordId());
                ps.setString(3, room.getTitle());
                ps.setString(4, room.getAddress());
                ps.setString(5, room.getDescription());
                ps.setInt(6, room.getArea());
                ps.setDouble(7, room.getPrice());
                ps.setBoolean(8, room.isStatus());
                ps.setBoolean(9, room.isAvailability());
                ps.executeUpdate();
            }

            // 2. Thêm ảnh
            insertImages(conn, room.getRoomId(), room.getImagePathList());

            // 3. Thêm tiện nghi
            insertAmenities(conn, room.getRoomId(), room.getAmenityList());

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            LOGGER.log(Level.SEVERE, "Lỗi thêm phòng: " + room.getRoomId(), e);
            return false;
        } finally {
            setAutoCommitTrue(conn);
        }
    }

    // ─────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────

    /**
     * Lấy tất cả phòng (không kèm ảnh / tiện nghi).
     */
    public List<RoomDTO> getAll() {
        List<RoomDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY created_at DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy danh sách phòng.", e);
        }
        return list;
    }

    /**
     * Lấy tất cả phòng kèm ảnh và tiện nghi đầy đủ.
     */
    public List<RoomDTO> getAllWithDetails() {
        List<RoomDTO> list = getAll();
        list.forEach(this::loadDetails);
        return list;
    }

    /**
     * Tìm phòng theo room_id (kèm chi tiết).
     */
    public RoomDTO getById(String roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RoomDTO room = mapRow(rs);
                    loadDetails(room);
                    return room;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm phòng: " + roomId, e);
        }
        return null;
    }

    /**
     * Lấy tất cả phòng của một landlord.
     */
    public List<RoomDTO> getByLandlordId(String landlordId) {
        List<RoomDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE landlord_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, landlordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomDTO room = mapRow(rs);
                    loadDetails(room);
                    list.add(room);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy phòng theo landlord: " + landlordId, e);
        }
        return list;
    }

    /**
     * Tìm phòng theo tên hoặc địa chỉ (LIKE search).
     */
    public List<RoomDTO> search(String keyword) {
        List<RoomDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE title LIKE ? OR address LIKE ? ORDER BY created_at DESC";
        String param = "%" + keyword + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, param);
            ps.setString(2, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm kiếm phòng: " + keyword, e);
        }
        return list;
    }

    /**
     * Lọc phòng theo khoảng giá.
     */
    public List<RoomDTO> filterByPrice(double minPrice, double maxPrice) {
        List<RoomDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE price BETWEEN ? AND ? ORDER BY price ASC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lọc phòng theo giá.", e);
        }
        return list;
    }

    /**
     * Lấy danh sách phòng đã được duyệt và còn trống.
     */
    public List<RoomDTO> getAvailableApprovedRooms() {
        List<RoomDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE status = TRUE AND availability = TRUE ORDER BY created_at DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                RoomDTO room = mapRow(rs);
                loadDetails(room);
                list.add(room);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy phòng available.", e);
        }
        return list;
    }

    // ─────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────

    /**
     * Cập nhật thông tin phòng, đồng thời làm mới ảnh và tiện nghi.
     * @return true nếu thành công.
     */
    public boolean update(RoomDTO room) {
        Connection conn = getConn();
        String sqlRoom = "UPDATE rooms SET landlord_id=?, title=?, address=?, description=?, "
                       + "area=?, price=?, status=?, availability=? WHERE room_id=?";
        try {
            conn.setAutoCommit(false);

            // 1. Cập nhật thông tin cơ bản
            try (PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setString(1, room.getLandlordId());
                ps.setString(2, room.getTitle());
                ps.setString(3, room.getAddress());
                ps.setString(4, room.getDescription());
                ps.setInt(5, room.getArea());
                ps.setDouble(6, room.getPrice());
                ps.setBoolean(7, room.isStatus());
                ps.setBoolean(8, room.isAvailability());
                ps.setString(9, room.getRoomId());
                ps.executeUpdate();
            }

            // 2. Làm mới ảnh (xóa cũ → thêm mới)
            deleteImages(conn, room.getRoomId());
            insertImages(conn, room.getRoomId(), room.getImagePathList());

            // 3. Làm mới tiện nghi (xóa cũ → thêm mới)
            deleteAmenityLinks(conn, room.getRoomId());
            insertAmenities(conn, room.getRoomId(), room.getAmenityList());

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật phòng: " + room.getRoomId(), e);
            return false;
        } finally {
            setAutoCommitTrue(conn);
        }
    }

    /**
     * Chỉ cập nhật trạng thái duyệt (status) của phòng.
     */
    public boolean updateStatus(String roomId, boolean status) {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setString(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật status phòng: " + roomId, e);
            return false;
        }
    }

    /**
     * Chỉ cập nhật trạng thái còn phòng (availability).
     */
    public boolean updateAvailability(String roomId, boolean availability) {
        String sql = "UPDATE rooms SET availability = ? WHERE room_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBoolean(1, availability);
            ps.setString(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật availability phòng: " + roomId, e);
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────

    /**
     * Xóa phòng (CASCADE tự xóa ảnh và room_amenities liên quan).
     * @return true nếu thành công.
     */
    public boolean delete(String roomId) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi xóa phòng: " + roomId, e);
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────

    /** Nạp ảnh + tiện nghi vào đối tượng RoomDTO đã có sẵn. */
    private void loadDetails(RoomDTO room) {
        room.setImagePathList(loadImages(room.getRoomId()));
        room.setAmenityList(loadAmenities(room.getRoomId()));
    }

    private List<String> loadImages(String roomId) {
        List<String> paths = new ArrayList<>();
        String sql = "SELECT image_path FROM room_images WHERE room_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) paths.add(rs.getString("image_path"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Lỗi nạp ảnh phòng: " + roomId, e);
        }
        return paths;
    }

    private List<AmenityDTO> loadAmenities(String roomId) {
        List<AmenityDTO> list = new ArrayList<>();
        String sql = "SELECT a.amenity_id, a.name FROM amenities a "
                   + "JOIN room_amenities ra ON a.amenity_id = ra.amenity_id WHERE ra.room_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AmenityDTO(rs.getString("amenity_id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Lỗi nạp amenities phòng: " + roomId, e);
        }
        return list;
    }

    private void insertImages(Connection conn, String roomId, List<String> paths) throws SQLException {
        if (paths == null || paths.isEmpty()) return;
        String sql = "INSERT INTO room_images (room_id, image_path) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String path : paths) {
                ps.setString(1, roomId);
                ps.setString(2, path);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertAmenities(Connection conn, String roomId, List<AmenityDTO> amenities) throws SQLException {
        if (amenities == null || amenities.isEmpty()) return;
        String sql = "INSERT INTO room_amenities (room_id, amenity_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (AmenityDTO a : amenities) {
                ps.setString(1, roomId);
                ps.setString(2, a.getAmenityId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteImages(Connection conn, String roomId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM room_images WHERE room_id = ?")) {
            ps.setString(1, roomId);
            ps.executeUpdate();
        }
    }

    private void deleteAmenityLinks(Connection conn, String roomId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM room_amenities WHERE room_id = ?")) {
            ps.setString(1, roomId);
            ps.executeUpdate();
        }
    }

    private void rollback(Connection conn) {
        try { conn.rollback(); } catch (SQLException ignored) {}
    }

    private void setAutoCommitTrue(Connection conn) {
        try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
    }

    private RoomDTO mapRow(ResultSet rs) throws SQLException {
        return new RoomDTO(
                rs.getString("room_id"),
                rs.getString("landlord_id"),
                rs.getString("title"),
                rs.getString("address"),
                rs.getString("description"),
                rs.getInt("area"),
                rs.getDouble("price"),
                rs.getBoolean("status"),
                rs.getBoolean("availability"),
                rs.getTimestamp("created_at") != null
                        ? rs.getTimestamp("created_at").toLocalDateTime()
                        : LocalDateTime.now()
        );
    }
}
