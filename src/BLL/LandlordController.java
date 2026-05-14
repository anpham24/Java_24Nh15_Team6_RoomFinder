package BLL;

import DAL.AmenityDAL;
import DAL.ReviewDAL;
import DAL.RoomDAL;
import DTO.RoomDTO;
import java.util.List;

public class LandlordController {

    private final RoomDAL    roomDAL    = new RoomDAL();
    private final AmenityDAL amenityDAL = new AmenityDAL();
    private final ReviewDAL  reviewDAL  = new ReviewDAL();

    public List<RoomDTO> getLandlordRooms(String landlordId) {
        List<RoomDTO> rooms = roomDAL.getByLandlordId(landlordId);
        rooms.forEach(r -> r.setAmenityList(amenityDAL.getByRoomId(r.getRoomId())));
        return rooms;
    }

    public double getAverageRating(String roomId) {
        return reviewDAL.getAverageRating(roomId);
    }

    public boolean deleteRoom(String roomId) {
        return roomDAL.delete(roomId);
    }

    public boolean updateAvailability(String roomId, boolean newAvail) {
        return roomDAL.updateAvailability(roomId, newAvail);
    }
}
