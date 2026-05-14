package DTO;

import java.time.LocalDateTime;

/**
 * DTO ánh xạ bảng reviews.
 */
public class ReviewDTO {

    private String        reviewId;
    private String        roomId;
    private String        tenantId;
    private int           rating;      // 1 – 5
    private String        comment;
    private LocalDateTime createdAt;

    public ReviewDTO() {}

    public ReviewDTO(String reviewId, String roomId, String tenantId,
                     int rating, String comment, LocalDateTime createdAt) {
        this.reviewId  = reviewId;
        this.roomId    = roomId;
        this.tenantId  = tenantId;
        this.rating    = rating;
        this.comment   = comment;
        this.createdAt = createdAt;
    }

    public String getReviewId()                { return reviewId; }
    public void setReviewId(String reviewId)   { this.reviewId = reviewId; }

    public String getRoomId()              { return roomId; }
    public void setRoomId(String roomId)   { this.roomId = roomId; }

    public String getTenantId()                { return tenantId; }
    public void setTenantId(String tenantId)   { this.tenantId = tenantId; }

    public int getRating()           { return rating; }
    public void setRating(int rating){ this.rating = rating; }

    public String getComment()               { return comment; }
    public void setComment(String comment)   { this.comment = comment; }

    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "ReviewDTO{reviewId='" + reviewId + "', roomId='" + roomId
                + "', tenantId='" + tenantId + "', rating=" + rating + "}";
    }
}
