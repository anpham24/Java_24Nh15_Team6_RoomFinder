package DAL;

import DTOs.AmenityDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomAmenityDAL {
    public List<String> findAmenityIdsByRoomId(String roomId) throws SQLException {
        String sql = "SELECT amenity_id FROM room_amenities WHERE room_id = ? ORDER BY amenity_id";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> ids = new ArrayList<>();
                while (resultSet.next()) {
                    ids.add(resultSet.getString("amenity_id"));
                }
                return ids;
            }
        }
    }

    public List<AmenityDTO> findAmenitiesByRoomId(String roomId) throws SQLException {
        String sql = "SELECT a.amenity_id, a.name "
                + "FROM amenities a "
                + "INNER JOIN room_amenities ra ON ra.amenity_id = a.amenity_id "
                + "WHERE ra.room_id = ? "
                + "ORDER BY a.name";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<AmenityDTO> amenities = new ArrayList<>();
                while (resultSet.next()) {
                    amenities.add(new AmenityDTO(
                            resultSet.getString("amenity_id"),
                            resultSet.getString("name")));
                }
                return amenities;
            }
        }
    }

    public void insert(Connection connection, String roomId, String amenityId) throws SQLException {
        String sql = "INSERT INTO room_amenities(room_id, amenity_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            statement.setString(2, amenityId);
            statement.executeUpdate();
        }
    }

    public void insertBatch(Connection connection, String roomId, List<String> amenityIds) throws SQLException {
        if (amenityIds == null || amenityIds.isEmpty()) {
            return;
        }

        for (String amenityId : amenityIds) {
            if (amenityId != null) {
                insert(connection, roomId, amenityId);
            }
        }
    }

    public int deleteByRoomId(Connection connection, String roomId) throws SQLException {
        String sql = "DELETE FROM room_amenities WHERE room_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            return statement.executeUpdate();
        }
    }

    public void replaceForRoom(Connection connection, String roomId, List<String> amenityIds) throws SQLException {
        deleteByRoomId(connection, roomId);
        insertBatch(connection, roomId, amenityIds);
    }
}
