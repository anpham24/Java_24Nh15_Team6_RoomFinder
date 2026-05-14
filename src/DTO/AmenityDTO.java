package DTO;

/**
 * DTO ánh xạ bảng amenities.
 */
public class AmenityDTO {

    private String amenityId;
    private String name;

    public AmenityDTO() {}

    public AmenityDTO(String amenityId, String name) {
        this.amenityId = amenityId;
        this.name      = name;
    }

    public String getAmenityId()              { return amenityId; }
    public void setAmenityId(String amenityId){ this.amenityId = amenityId; }

    public String getName()           { return name; }
    public void setName(String name)  { this.name = name; }

    @Override
    public String toString() {
        return "AmenityDTO{amenityId='" + amenityId + "', name='" + name + "'}";
    }
}
