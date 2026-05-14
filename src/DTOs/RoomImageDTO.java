package DTOs;

public class RoomImageDTO {
    private int imageId;
    private int roomId;
    private String imagePath;

    public RoomImageDTO() {
    }

    public RoomImageDTO(int imageId, int roomId, String imagePath) {
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

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
