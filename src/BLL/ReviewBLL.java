package BLL;

import DAL.ReviewDAL;
import DAL.RoomDAL;
import DTOs.ReviewDTO;
import DTOs.Role;
import DTOs.RoomDTO;
import DTOs.UserDTO;
import java.sql.SQLException;
import java.util.List;

public class ReviewBLL {
    private final ReviewDAL reviewDAL = new ReviewDAL();
    private final RoomDAL roomDAL = new RoomDAL();

    public List<ReviewDTO> getReviewsByRoomId(int roomId) throws SQLException {
        if (roomId <= 0) {
            throw new IllegalArgumentException("Valid room id is required");
        }
        return reviewDAL.findByRoomId(roomId);
    }

    public int addReview(int roomId, int rating, String comment) throws SQLException {
        UserDTO tenant = SessionContext.requireRole(Role.TENANT);
        validateReview(roomId, rating, comment);

        RoomDTO room = roomDAL.findById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room does not exist");
        }
        if (!room.isStatus()) {
            throw new SecurityException("Room is not approved");
        }

        ReviewDTO existing = reviewDAL.findByTenantAndRoom(tenant.getUserId(), roomId);
        if (existing != null) {
            throw new IllegalArgumentException("You already reviewed this room");
        }

        ReviewDTO review = new ReviewDTO();
        review.setRoomId(roomId);
        review.setTenantId(tenant.getUserId());
        review.setRating(rating);
        review.setComment(comment.trim());
        return reviewDAL.insert(review);
    }

    public boolean updateReview(int reviewId, int rating, String comment) throws SQLException {
        UserDTO tenant = SessionContext.requireRole(Role.TENANT);
        if (reviewId <= 0) {
            throw new IllegalArgumentException("Valid review id is required");
        }

        ReviewDTO existing = reviewDAL.findById(reviewId);
        if (existing == null) {
            return false;
        }
        if (existing.getTenantId() != tenant.getUserId()) {
            throw new SecurityException("Permission denied");
        }

        validateReview(existing.getRoomId(), rating, comment);
        existing.setRating(rating);
        existing.setComment(comment.trim());
        return reviewDAL.update(existing);
    }

    public boolean deleteReview(int reviewId) throws SQLException {
        UserDTO user = SessionContext.requireAnyRole(Role.ADMIN, Role.TENANT);
        if (reviewId <= 0) {
            throw new IllegalArgumentException("Valid review id is required");
        }

        ReviewDTO existing = reviewDAL.findById(reviewId);
        if (existing == null) {
            return false;
        }
        if (user.getRole() != Role.ADMIN && existing.getTenantId() != user.getUserId()) {
            throw new SecurityException("Permission denied");
        }

        return reviewDAL.delete(reviewId);
    }

    private void validateReview(int roomId, int rating, String comment) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("Valid room id is required");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be from 1 to 5");
        }
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Review comment is required");
        }
    }
}
