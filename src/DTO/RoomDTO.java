package DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO ánh xạ bảng rooms.
 * Bao gồm danh sách ảnh (imagePathList) và tiện nghi (amenityList)
 * để tiện thao tác ở tầng BLL / View.
 */
public class RoomDTO {

    private String roomId;
    private String landlordId;
    private String title;
    private String address;
    private String description;
    private int    area;
    private double price;
    /** Trạng thái duyệt: "PENDING" | "APPROVED" | "DECLINED" */
    private String status;
    private boolean availability;    // true = còn phòng
    private LocalDateTime createdAt;

    // Dữ liệu quan hệ – được nạp khi cần
    private List<String>      imagePathList = new ArrayList<>();
    private List<AmenityDTO>  amenityList   = new ArrayList<>();

    public RoomDTO() {}

    public RoomDTO(String roomId, String landlordId, String title, String address,
                   String description, int area, double price,
                   String status, boolean availability, LocalDateTime createdAt) {
        this.roomId       = roomId;
        this.landlordId   = landlordId;
        this.title        = title;
        this.address      = address;
        this.description  = description;
        this.area         = area;
        this.price        = price;
        this.status       = status;
        this.availability = availability;
        this.createdAt    = createdAt;
    }

    // ---- Getters & Setters ----

    public String getRoomId()              { return roomId; }
    public void setRoomId(String roomId)   { this.roomId = roomId; }

    public String getLandlordId()                  { return landlordId; }
    public void setLandlordId(String landlordId)   { this.landlordId = landlordId; }

    public String getTitle()             { return title; }
    public void setTitle(String title)   { this.title = title; }

    public String getAddress()               { return address; }
    public void setAddress(String address)   { this.address = address; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public int getArea()          { return area; }
    public void setArea(int area) { this.area = area; }

    public double getPrice()            { return price; }
    public void setPrice(double price)  { this.price = price; }

    public String getStatus()             { return status; }
    public void setStatus(String status)   { this.status = status; }

    public boolean isAvailability()                    { return availability; }
    public void setAvailability(boolean availability)  { this.availability = availability; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)    { this.createdAt = createdAt; }

    public List<String> getImagePathList()                         { return imagePathList; }
    public void setImagePathList(List<String> imagePathList)       { this.imagePathList = imagePathList; }

    public List<AmenityDTO> getAmenityList()                       { return amenityList; }
    public void setAmenityList(List<AmenityDTO> amenityList)       { this.amenityList = amenityList; }

    @Override
    public String toString() {
        return "RoomDTO{roomId='" + roomId + "', title='" + title
                + "', price=" + price + ", status='" + status + "'}";
    }
}
