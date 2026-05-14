package DTOs;

public class RoomImageDTO {
    private int imageId;
    private String roomId;
    private String imagePath;

    public RoomImageDTO() {
    }

    public RoomImageDTO(int imageId, String roomId, String imagePath) {
        this.imageId = imageId;
        this.roomId = roomId;
        this.imagePath = imagePath;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
