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
            throw new IllegalArgumentException("ID phòng hợp lệ là bắt buộc");
        }
        return reviewDAL.findByRoomId(roomId);
    }

    public int addReview(int roomId, int rating, String comment) throws SQLException {
        UserDTO tenant = SessionContext.requireRole(Role.TENANT);
        validateReview(roomId, rating, comment);

        RoomDTO room = roomDAL.findById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Phòng không tồn tại");
        }
        if (!room.isStatus()) {
            throw new SecurityException("Phòng chưa được duyệt");
        }

        ReviewDTO existing = reviewDAL.findByTenantAndRoom(tenant.getUserId(), roomId);
        if (existing != null) {
            throw new IllegalArgumentException("Bạn đã đánh giá phòng này");
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
            throw new IllegalArgumentException("ID đánh giá hợp lệ là bắt buộc");
        }

        ReviewDTO existing = reviewDAL.findById(reviewId);
        if (existing == null) {
            return false;
        }
        if (existing.getTenantId() != tenant.getUserId()) {
            throw new SecurityException("Quyền truy cập bị từ chối");
        }

        validateReview(existing.getRoomId(), rating, comment);
        existing.setRating(rating);
        existing.setComment(comment.trim());
        return reviewDAL.update(existing);
    }

    public boolean deleteReview(int reviewId) throws SQLException {
        UserDTO user = SessionContext.requireAnyRole(Role.ADMIN, Role.TENANT);
        if (reviewId <= 0) {
            throw new IllegalArgumentException("ID đánh giá hợp lệ là bắt buộc");
        }

        ReviewDTO existing = reviewDAL.findById(reviewId);
        if (existing == null) {
            return false;
        }
        if (user.getRole() != Role.ADMIN && existing.getTenantId() != user.getUserId()) {
            throw new SecurityException("Quyền truy cập bị từ chối");
        }

        return reviewDAL.delete(reviewId);
    }

    private void validateReview(int roomId, int rating, String comment) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("ID phòng hợp lệ là bắt buộc");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Đánh giá phải từ 1 đến 5");
        }
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Bình luận đánh giá là bắt buộc");
        }
    }
}
