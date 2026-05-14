package DTOs;

public class AmenityDTO {
    private int amenityId;
    private String name;

    public AmenityDTO() {
    }

    public AmenityDTO(int amenityId, String name) {
        this.amenityId = amenityId;
        this.name = name;
    }

    public int getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(int amenityId) {
        this.amenityId = amenityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
