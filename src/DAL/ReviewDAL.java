package DAL;

import DTO.ReviewDTO;
import Utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReviewDAL {

    private static final Logger LOGGER = Logger.getLogger(ReviewDAL.class.getName());

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean insert(ReviewDTO review) {
        String sql = "INSERT INTO reviews (review_id, room_id, tenant_id, rating, comment) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, review.getReviewId());
            ps.setString(2, review.getRoomId());
            ps.setString(3, review.getTenantId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi thêm review: " + review.getReviewId(), e);
            return false;
        }
    }

    public List<ReviewDTO> getAll() {
        List<ReviewDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM reviews ORDER BY created_at DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy danh sách reviews.", e);
        }
        return list;
    }

    public List<ReviewDTO> getByRoomId(String roomId) {
        List<ReviewDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE room_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy reviews của phòng: " + roomId, e);
        }
        return list;
    }

    public List<ReviewDTO> getByTenantId(String tenantId) {
        List<ReviewDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE tenant_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy reviews của tenant: " + tenantId, e);
        }
        return list;
    }

    public double getAverageRating(String roomId) {
        String sql = "SELECT AVG(rating) AS avg_rating FROM reviews WHERE room_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tính điểm trung bình phòng: " + roomId, e);
        }
        return 0.0;
    }

    public boolean update(ReviewDTO review) {
        String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setString(3, review.getReviewId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật review: " + review.getReviewId(), e);
            return false;
        }
    }

    public boolean delete(String reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi xóa review: " + reviewId, e);
            return false;
        }
    }

    private ReviewDTO mapRow(ResultSet rs) throws SQLException {
        LocalDateTime createdAt = rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime()
                : LocalDateTime.now();
        return new ReviewDTO(
                rs.getString("review_id"),
                rs.getString("room_id"),
                rs.getString("tenant_id"),
                rs.getInt("rating"),
                rs.getString("comment"),
                createdAt
        );
    }
}
