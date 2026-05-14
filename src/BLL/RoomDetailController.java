package BLL;

import DAL.ReviewDAL;
import DAL.RoomDAL;
import DAL.UserDAL;
import DTO.ReviewDTO;
import DTO.RoomDTO;
import DTO.UserDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RoomDetailController {

    private final RoomDAL   roomDAL   = new RoomDAL();
    private final ReviewDAL reviewDAL = new ReviewDAL();
    private final UserDAL   userDAL   = new UserDAL();

    public double getAverageRating(String roomId) {
        return reviewDAL.getAverageRating(roomId);
    }

    public List<ReviewDTO> getReviews(String roomId) {
        return reviewDAL.getByRoomId(roomId);
    }

    public UserDTO getUserById(String userId) {
        return userDAL.getById(userId);
    }

    public String submitReview(String roomId, String tenantId, int rating, String comment) {
        ReviewDTO review = new ReviewDTO(UUID.randomUUID().toString(), roomId, tenantId,
                rating, comment, LocalDateTime.now());
        return reviewDAL.insert(review) ? null : "Đăng đánh giá thất bại.";
    }

    public boolean deleteRoom(String roomId) {
        return roomDAL.delete(roomId);
    }

    public boolean updateAvailability(String roomId, boolean newAvail) {
        return roomDAL.updateAvailability(roomId, newAvail);
    }

    public RoomDTO refreshRoom(String roomId) {
        return roomDAL.getById(roomId);
    }
}
