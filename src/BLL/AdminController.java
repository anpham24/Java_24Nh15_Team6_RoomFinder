package BLL;

import DAL.AccountDAL;
import DAL.AmenityDAL;
import DAL.RoomDAL;
import DAL.UserDAL;
import DTO.AmenityDTO;
import DTO.RoomDTO;
import DTO.UserDTO;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdminController {

    private final RoomDAL    roomDAL    = new RoomDAL();
    private final UserDAL    userDAL    = new UserDAL();
    private final AmenityDAL amenityDAL = new AmenityDAL();
    private final AccountDAL accountDAL = new AccountDAL();

    public List<RoomDTO> getPendingRooms() {
        return roomDAL.getByStatus("PENDING");
    }

    public boolean updateRoomStatus(String roomId, String status) {
        return roomDAL.updateStatus(roomId, status);
    }

    public UserDTO getUserById(String userId) {
        return userDAL.getById(userId);
    }

    public RoomDTO getRoomById(String roomId) {
        return roomDAL.getById(roomId);
    }

    public List<RoomDTO> searchRooms(String keyword, String statusFilter) {
        return roomDAL.getAll().stream()
            .filter(r -> {
                if (keyword != null && !keyword.isEmpty())
                    if (!r.getTitle().toLowerCase().contains(keyword.toLowerCase())
                            && !r.getAddress().toLowerCase().contains(keyword.toLowerCase())) return false;
                if (statusFilter != null && !statusFilter.equals(r.getStatus())) return false;
                return true;
            })
            .collect(Collectors.toList());
    }

    public boolean deleteRoom(String roomId) {
        return roomDAL.delete(roomId);
    }

    public List<UserDTO> searchUsers(String keyword, UserDTO.Role roleFilter) {
        return userDAL.getAll().stream()
            .filter(u -> u.getRole() != UserDTO.Role.ADMIN)
            .filter(u -> {
                if (keyword != null && !keyword.isEmpty())
                    if (!u.getName().toLowerCase().contains(keyword.toLowerCase())
                            && !u.getUsername().toLowerCase().contains(keyword.toLowerCase())) return false;
                if (roleFilter != null && u.getRole() != roleFilter) return false;
                return true;
            })
            .collect(Collectors.toList());
    }

    public boolean deleteUser(String userId) {
        UserDTO user = userDAL.getById(userId);
        if (user == null) return false;
        return accountDAL.delete(user.getUsername());
    }

    public List<AmenityDTO> getAllAmenities() {
        return amenityDAL.getAll();
    }

    public boolean addAmenity(String name) {
        return amenityDAL.insert(new AmenityDTO(UUID.randomUUID().toString(), name));
    }

    public boolean updateAmenity(String amenityId, String name) {
        return amenityDAL.update(new AmenityDTO(amenityId, name));
    }

    public boolean deleteAmenity(String amenityId) {
        return amenityDAL.delete(amenityId);
    }
}
