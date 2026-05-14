package DTOs;

import java.time.LocalDateTime;

public class RoomDTO {
    private int roomId;
    private int landlordId;
    private String title;
    private String address;
    private String description;
    private double area;
    private double price;
    private boolean status;
    private boolean availability;
    private LocalDateTime createdAt;
    private double averageRating;
    private int reviewCount;

    public RoomDTO() {
    }

    public RoomDTO(int roomId, int landlordId, String title, String address, String description,
            double area, double price, boolean status, boolean availability, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.landlordId = landlordId;
        this.title = title;
        this.address = address;
        this.description = description;
        this.area = area;
        this.price = price;
        this.status = status;
        this.availability = availability;
        this.createdAt = createdAt;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(int landlordId) {
        this.landlordId = landlordId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isApproved() {
        return status;
    }

    public void setApproved(boolean approved) {
        this.status = approved;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
