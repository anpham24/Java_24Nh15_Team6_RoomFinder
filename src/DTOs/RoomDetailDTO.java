package DTOs;

import java.util.ArrayList;
import java.util.List;

public class RoomDetailDTO {
    private RoomDTO room;
    private UserDTO landlord;
    private List<AmenityDTO> amenities = new ArrayList<>();
    private List<RoomImageDTO> images = new ArrayList<>();
    private List<ReviewDTO> reviews = new ArrayList<>();
    private double averageRating;
    private int reviewCount;

    public RoomDetailDTO() {
    }

    public RoomDetailDTO(RoomDTO room, UserDTO landlord, List<AmenityDTO> amenities,
            List<RoomImageDTO> images, List<ReviewDTO> reviews) {
        this.room = room;
        this.landlord = landlord;
        setAmenities(amenities);
        setImages(images);
        setReviews(reviews);
    }

    public RoomDTO getRoom() {
        return room;
    }

    public void setRoom(RoomDTO room) {
        this.room = room;
    }

    public UserDTO getLandlord() {
        return landlord;
    }

    public void setLandlord(UserDTO landlord) {
        this.landlord = landlord;
    }

    public List<AmenityDTO> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<AmenityDTO> amenities) {
        this.amenities = amenities == null ? new ArrayList<>() : new ArrayList<>(amenities);
    }

    public List<RoomImageDTO> getImages() {
        return images;
    }

    public void setImages(List<RoomImageDTO> images) {
        this.images = images == null ? new ArrayList<>() : new ArrayList<>(images);
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews == null ? new ArrayList<>() : new ArrayList<>(reviews);
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
