package DAL;

import DTOs.ReviewDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAL {
    public List<ReviewDTO> findByRoomId(int roomId) throws SQLException {
        String sql = "SELECT r.review_id, r.room_id, r.tenant_id, r.rating, r.comment, r.created_at, "
                + "u.name AS tenant_name "
                + "FROM reviews r "
                + "LEFT JOIN users u ON u.user_id = r.tenant_id "
                + "WHERE r.room_id = ? "
                + "ORDER BY r.created_at DESC, r.review_id DESC";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapReviews(resultSet);
            }
        }
    }

    public ReviewDTO findById(int reviewId) throws SQLException {
        String sql = "SELECT r.review_id, r.room_id, r.tenant_id, r.rating, r.comment, r.created_at, "
                + "u.name AS tenant_name "
                + "FROM reviews r "
                + "LEFT JOIN users u ON u.user_id = r.tenant_id "
                + "WHERE r.review_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapReview(resultSet);
                }
            }
        }
        return null;
    }

    public ReviewDTO findByTenantAndRoom(int tenantId, int roomId) throws SQLException {
        String sql = "SELECT r.review_id, r.room_id, r.tenant_id, r.rating, r.comment, r.created_at, "
                + "u.name AS tenant_name "
                + "FROM reviews r "
                + "LEFT JOIN users u ON u.user_id = r.tenant_id "
                + "WHERE r.tenant_id = ? AND r.room_id = ? "
                + "ORDER BY r.created_at DESC, r.review_id DESC "
                + "LIMIT 1";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tenantId);
            statement.setInt(2, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapReview(resultSet);
                }
            }
        }
        return null;
    }

    public int insert(ReviewDTO review) throws SQLException {
        String sql = "INSERT INTO reviews(room_id, tenant_id, rating, comment, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, review.getRoomId());
            statement.setInt(2, review.getTenantId());
            statement.setInt(3, review.getRating());
            statement.setString(4, review.getComment());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    review.setReviewId(id);
                    return id;
                }
            }
        }
        return 0;
    }

    public boolean update(ReviewDTO review) throws SQLException {
        String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, review.getRating());
            statement.setString(2, review.getComment());
            statement.setInt(3, review.getReviewId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int reviewId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            return statement.executeUpdate() > 0;
        }
    }

    public int deleteByRoomId(Connection connection, int roomId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE room_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            return statement.executeUpdate();
        }
    }

    public double getAverageRating(int roomId) throws SQLException {
        String sql = "SELECT COALESCE(AVG(rating), 0) AS average_rating FROM reviews WHERE room_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("average_rating");
                }
            }
        }
        return 0;
    }

    public int countByRoomId(int roomId) throws SQLException {
        String sql = "SELECT COUNT(*) AS review_count FROM reviews WHERE room_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
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
                resultSet.getInt("review_id"),
                resultSet.getInt("room_id"),
                resultSet.getInt("tenant_id"),
                resultSet.getInt("rating"),
                resultSet.getString("comment"),
                createdAt == null ? null : createdAt.toLocalDateTime());
        review.setTenantName(resultSet.getString("tenant_name"));
        return review;
    }
}
