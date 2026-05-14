package BLL;

import DAL.DBConnection;
import DAL.ReviewDAL;
import DAL.RoomAmenityDAL;
import DAL.RoomDAL;
import DAL.RoomImageDAL;
import DAL.UserDAL;
import DTOs.AmenityDTO;
import DTOs.ReviewDTO;
import DTOs.Role;
import DTOs.RoomDTO;
import DTOs.RoomDetailDTO;
import DTOs.RoomImageDTO;
import DTOs.RoomSearchCriteriaDTO;
import DTOs.UserDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomBLL {
    private final RoomDAL roomDAL = new RoomDAL();
    private final UserDAL userDAL = new UserDAL();
    private final RoomAmenityDAL roomAmenityDAL = new RoomAmenityDAL();
    private final RoomImageDAL roomImageDAL = new RoomImageDAL();
    private final ReviewDAL reviewDAL = new ReviewDAL();

    public List<RoomDTO> getAvailableRooms() throws SQLException {
        SessionContext.requireRole(Role.TENANT);
        RoomSearchCriteriaDTO criteria = new RoomSearchCriteriaDTO();
        criteria.setStatus(Boolean.TRUE);
        criteria.setAvailability(Boolean.TRUE);
        criteria.setSortBy(RoomSearchCriteriaDTO.SortBy.CREATED_AT_DESC);
        return roomDAL.search(criteria);
    }

    public List<RoomDTO> searchAvailableRooms(RoomSearchCriteriaDTO criteria) throws SQLException {
        SessionContext.requireRole(Role.TENANT);
        RoomSearchCriteriaDTO tenantCriteria = copyCriteria(criteria);
        tenantCriteria.setStatus(Boolean.TRUE);
        tenantCriteria.setAvailability(Boolean.TRUE);
        validateSearchCriteria(tenantCriteria);
        return roomDAL.search(tenantCriteria);
    }

    public List<RoomDTO> getRoomsForCurrentLandlord() throws SQLException {
        UserDTO landlord = SessionContext.requireRole(Role.LANDLORD);
        return roomDAL.findByLandlordId(landlord.getUserId());
    }

    public List<RoomDTO> getPendingRooms() throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        return roomDAL.findPending();
    }

    public List<RoomDTO> searchRoomsForAdmin(RoomSearchCriteriaDTO criteria) throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        RoomSearchCriteriaDTO adminCriteria = copyCriteria(criteria);
        validateSearchCriteria(adminCriteria);
        return roomDAL.search(adminCriteria);
    }

    public RoomDetailDTO getRoomDetail(int roomId) throws SQLException {
        if (roomId <= 0) {
            throw new IllegalArgumentException("Valid room id is required");
        }

        UserDTO currentUser = SessionContext.requireLogin();
        RoomDTO room = roomDAL.findById(roomId);
        if (room == null) {
            return null;
        }
        ensureCanViewRoom(currentUser, room);

        UserDTO landlord = userDAL.findById(room.getLandlordId());
        List<AmenityDTO> amenities = roomAmenityDAL.findAmenitiesByRoomId(roomId);
        List<RoomImageDTO> images = roomImageDAL.findByRoomId(roomId);
        List<ReviewDTO> reviews = reviewDAL.findByRoomId(roomId);

        RoomDetailDTO detail = new RoomDetailDTO(room, landlord, amenities, images, reviews);
        detail.setAverageRating(room.getAverageRating());
        detail.setReviewCount(room.getReviewCount());
        return detail;
    }

    public int createRoom(RoomDetailDTO detail) throws SQLException {
        UserDTO landlord = SessionContext.requireRole(Role.LANDLORD);
        RoomDTO room = requireRoom(detail);
        validateRoom(room);

        room.setLandlordId(landlord.getUserId());
        room.setStatus(false);
        room.setAvailability(true);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int roomId = roomDAL.insert(connection, room);
                roomAmenityDAL.insertBatch(connection, roomId, extractAmenityIds(detail));
                roomImageDAL.insertBatch(connection, roomId, extractImages(detail));
                connection.commit();
                return roomId;
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean updateRoom(RoomDetailDTO detail) throws SQLException {
        UserDTO landlord = SessionContext.requireRole(Role.LANDLORD);
        RoomDTO room = requireRoom(detail);
        if (room.getRoomId() <= 0) {
            throw new IllegalArgumentException("Valid room id is required");
        }
        validateRoom(room);

        RoomDTO existing = roomDAL.findById(room.getRoomId());
        if (existing == null) {
            return false;
        }
        if (existing.getLandlordId() != landlord.getUserId()) {
            throw new SecurityException("Permission denied");
        }

        room.setLandlordId(existing.getLandlordId());
        room.setAvailability(existing.isAvailability());
        room.setStatus(false);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                boolean updated = roomDAL.update(connection, room);
                roomAmenityDAL.replaceForRoom(connection, room.getRoomId(), extractAmenityIds(detail));
                roomImageDAL.replaceForRoom(connection, room.getRoomId(), extractImages(detail));
                connection.commit();
                return updated;
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean deleteRoom(int roomId) throws SQLException {
        UserDTO user = SessionContext.requireAnyRole(Role.ADMIN, Role.LANDLORD);
        RoomDTO room = roomDAL.findById(roomId);
        if (room == null) {
            return false;
        }
        if (user.getRole() != Role.ADMIN && room.getLandlordId() != user.getUserId()) {
            throw new SecurityException("Permission denied");
        }
        return deleteRoomCascade(roomId);
    }

    public boolean updateAvailability(int roomId, boolean availability) throws SQLException {
        UserDTO landlord = SessionContext.requireRole(Role.LANDLORD);
        RoomDTO room = roomDAL.findById(roomId);
        if (room == null) {
            return false;
        }
        if (room.getLandlordId() != landlord.getUserId()) {
            throw new SecurityException("Permission denied");
        }
        return roomDAL.updateAvailability(roomId, availability);
    }

    public boolean approveRoom(int roomId) throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        if (roomId <= 0) {
            throw new IllegalArgumentException("Valid room id is required");
        }
        return roomDAL.approve(roomId);
    }

    public boolean declineRoom(int roomId) throws SQLException {
        SessionContext.requireRole(Role.ADMIN);
        if (roomId <= 0) {
            throw new IllegalArgumentException("Valid room id is required");
        }
        return deleteRoomCascade(roomId);
    }

    private boolean deleteRoomCascade(int roomId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                reviewDAL.deleteByRoomId(connection, roomId);
                roomImageDAL.deleteByRoomId(connection, roomId);
                roomAmenityDAL.deleteByRoomId(connection, roomId);
                boolean deleted = roomDAL.delete(connection, roomId);
                connection.commit();
                return deleted;
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void ensureCanViewRoom(UserDTO user, RoomDTO room) {
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (user.getRole() == Role.LANDLORD && room.getLandlordId() == user.getUserId()) {
            return;
        }
        if (user.getRole() == Role.TENANT && room.isStatus() && room.isAvailability()) {
            return;
        }
        throw new SecurityException("Permission denied");
    }

    private RoomDTO requireRoom(RoomDetailDTO detail) {
        if (detail == null || detail.getRoom() == null) {
            throw new IllegalArgumentException("Room data is required");
        }
        return detail.getRoom();
    }

    private void validateRoom(RoomDTO room) {
        if (room.getTitle() == null || room.getTitle().isBlank()) {
            throw new IllegalArgumentException("Room title is required");
        }
        if (room.getAddress() == null || room.getAddress().isBlank()) {
            throw new IllegalArgumentException("Room address is required");
        }
        if (room.getDescription() == null || room.getDescription().isBlank()) {
            throw new IllegalArgumentException("Room description is required");
        }
        if (room.getArea() <= 0) {
            throw new IllegalArgumentException("Room area must be greater than 0");
        }
        if (room.getPrice() <= 0) {
            throw new IllegalArgumentException("Room price must be greater than 0");
        }
    }

    private void validateSearchCriteria(RoomSearchCriteriaDTO criteria) {
        if (criteria.getMinPrice() != null && criteria.getMinPrice() < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        if (criteria.getMaxPrice() != null && criteria.getMaxPrice() < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        if (criteria.getMinPrice() != null && criteria.getMaxPrice() != null
                && criteria.getMinPrice() > criteria.getMaxPrice()) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
    }

    private RoomSearchCriteriaDTO copyCriteria(RoomSearchCriteriaDTO criteria) {
        RoomSearchCriteriaDTO copy = new RoomSearchCriteriaDTO();
        if (criteria == null) {
            return copy;
        }
        copy.setKeyword(criteria.getKeyword());
        copy.setMinPrice(criteria.getMinPrice());
        copy.setMaxPrice(criteria.getMaxPrice());
        copy.setAmenityIds(criteria.getAmenityIds());
        copy.setSortBy(criteria.getSortBy());
        copy.setStatus(criteria.getStatus());
        copy.setAvailability(criteria.getAvailability());
        copy.setLandlordId(criteria.getLandlordId());
        return copy;
    }

    private List<Integer> extractAmenityIds(RoomDetailDTO detail) {
        List<Integer> ids = new ArrayList<>();
        if (detail.getAmenities() == null) {
            return ids;
        }

        for (AmenityDTO amenity : detail.getAmenities()) {
            if (amenity != null && amenity.getAmenityId() > 0 && !ids.contains(amenity.getAmenityId())) {
                ids.add(amenity.getAmenityId());
            }
        }
        return ids;
    }

    private List<RoomImageDTO> extractImages(RoomDetailDTO detail) {
        List<RoomImageDTO> images = new ArrayList<>();
        if (detail.getImages() == null) {
            return images;
        }

        for (RoomImageDTO image : detail.getImages()) {
            if (image != null && image.getImagePath() != null && !image.getImagePath().isBlank()) {
                images.add(new RoomImageDTO(image.getImageId(), image.getRoomId(), image.getImagePath().trim()));
            }
        }
        return images;
    }
}
