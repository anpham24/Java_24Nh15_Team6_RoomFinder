package DAL;

import DTOs.ReviewDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReviewDAL {
    public List<ReviewDTO> findByRoomId(String roomId) throws SQLException {
        String sql = "SELECT r.review_id, r.room_id, r.tenant_id, r.rating, r.comment, r.created_at, "
                + "u.name AS tenant_name "
                + "FROM reviews r "
                + "LEFT JOIN users u ON u.user_id = r.tenant_id "
                + "WHERE r.room_id = ? "
                + "ORDER BY r.created_at DESC, r.review_id DESC";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapReviews(resultSet);
            }
        }
    }

    public ReviewDTO findById(String reviewId) throws SQLException {
        String sql = "SELECT r.review_id, r.room_id, r.tenant_id, r.rating, r.comment, r.created_at, "
                + "u.name AS tenant_name "
                + "FROM reviews r "
                + "LEFT JOIN users u ON u.user_id = r.tenant_id "
                + "WHERE r.review_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reviewId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapReview(resultSet);
                }
            }
        }
        return null;
    }

    public ReviewDTO findByTenantAndRoom(String tenantId, String roomId) throws SQLException {
        String sql = "SELECT r.review_id, r.room_id, r.tenant_id, r.rating, r.comment, r.created_at, "
                + "u.name AS tenant_name "
                + "FROM reviews r "
                + "LEFT JOIN users u ON u.user_id = r.tenant_id "
                + "WHERE r.tenant_id = ? AND r.room_id = ? "
                + "ORDER BY r.created_at DESC, r.review_id DESC "
                + "LIMIT 1";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tenantId);
            statement.setString(2, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapReview(resultSet);
                }
            }
        }
        return null;
    }

    public String insert(ReviewDTO review) throws SQLException {
        String reviewId = UUID.randomUUID().toString();
        String sql = "INSERT INTO reviews(review_id, room_id, tenant_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reviewId);
            statement.setString(2, review.getRoomId());
            statement.setString(3, review.getTenantId());
            statement.setInt(4, review.getRating());
            statement.setString(5, review.getComment());
            statement.executeUpdate();
            review.setReviewId(reviewId);
            return reviewId;
        }
    }

    public boolean update(ReviewDTO review) throws SQLException {
        String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, review.getRating());
            statement.setString(2, review.getComment());
            statement.setString(3, review.getReviewId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(String reviewId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reviewId);
            return statement.executeUpdate() > 0;
        }
    }

    public int deleteByRoomId(Connection connection, String roomId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE room_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            return statement.executeUpdate();
        }
    }

    public double getAverageRating(String roomId) throws SQLException {
        String sql = "SELECT COALESCE(AVG(rating), 0) AS average_rating FROM reviews WHERE room_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("average_rating");
                }
            }
        }
        return 0;
    }

    public int countByRoomId(String roomId) throws SQLException {
        String sql = "SELECT COUNT(*) AS review_count FROM reviews WHERE room_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("review_count");
                }
            }
        }
        return 0;
    }

    private List<ReviewDTO> mapReviews(ResultSet resultSet) throws SQLException {
        List<ReviewDTO> reviews = new ArrayList<>();
        while (resultSet.next()) {
            reviews.add(mapReview(resultSet));
        }
        return reviews;
    }

    private ReviewDTO mapReview(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        ReviewDTO review = new ReviewDTO(
                resultSet.getString("review_id"),
                resultSet.getString("room_id"),
                resultSet.getString("tenant_id"),
                resultSet.getInt("rating"),
                resultSet.getString("comment"),
                createdAt == null ? null : createdAt.toLocalDateTime());
        review.setTenantName(resultSet.getString("tenant_name"));
        return review;
    }
}
