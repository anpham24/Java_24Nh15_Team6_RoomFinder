package BLL;

import DAL.AmenityDAL;
import DAL.ReviewDAL;
import DAL.RoomDAL;
import DTO.AmenityDTO;
import DTO.RoomDTO;
import java.util.List;

public class TenantController {

    private final RoomDAL    roomDAL    = new RoomDAL();
    private final AmenityDAL amenityDAL = new AmenityDAL();
    private final ReviewDAL  reviewDAL  = new ReviewDAL();

    public List<AmenityDTO> getAllAmenities() {
        return amenityDAL.getAll();
    }

    public List<RoomDTO> getRooms(String keyword, double minPrice, double maxPrice,
                                   java.util.List<String> amenityIds,
                                   boolean sortByPrice, boolean sortByRating) {
        List<RoomDTO> rooms = roomDAL.filterAdvanced(keyword, minPrice, maxPrice, amenityIds, sortByPrice, sortByRating);
        rooms.forEach(r -> r.setAmenityList(amenityDAL.getByRoomId(r.getRoomId())));
        return rooms;
    }

    public double getAverageRating(String roomId) {
        return reviewDAL.getAverageRating(roomId);
    }
}
