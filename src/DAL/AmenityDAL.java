package DAL;

import DTOs.AmenityDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AmenityDAL {
    public List<AmenityDTO> findAll() throws SQLException {
        String sql = "SELECT amenity_id, name FROM amenities ORDER BY name";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            return mapAmenities(resultSet);
        }
    }

    public AmenityDTO findById(String amenityId) throws SQLException {
        String sql = "SELECT amenity_id, name FROM amenities WHERE amenity_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, amenityId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAmenity(resultSet);
                }
            }
        }
        return null;
    }

    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT 1 FROM amenities WHERE LOWER(name) = LOWER(?)";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public List<AmenityDTO> findByRoomId(String roomId) throws SQLException {
        String sql = "SELECT a.amenity_id, a.name "
                + "FROM amenities a "
                + "INNER JOIN room_amenities ra ON ra.amenity_id = a.amenity_id "
                + "WHERE ra.room_id = ? "
                + "ORDER BY a.name";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapAmenities(resultSet);
            }
        }
    }

    public String insert(AmenityDTO amenity) throws SQLException {
        String amenityId = UUID.randomUUID().toString();
        String sql = "INSERT INTO amenities(amenity_id, name) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, amenityId);
            statement.setString(2, amenity.getName());
            statement.executeUpdate();
            amenity.setAmenityId(amenityId);
            return amenityId;
        }
    }

    public boolean update(AmenityDTO amenity) throws SQLException {
        String sql = "UPDATE amenities SET name = ? WHERE amenity_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, amenity.getName());
            statement.setString(2, amenity.getAmenityId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(String amenityId) throws SQLException {
        String sql = "DELETE FROM amenities WHERE amenity_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, amenityId);
            return statement.executeUpdate() > 0;
        }
    }

    private List<AmenityDTO> mapAmenities(ResultSet resultSet) throws SQLException {
        List<AmenityDTO> amenities = new ArrayList<>();
        while (resultSet.next()) {
            amenities.add(mapAmenity(resultSet));
        }
        return amenities;
    }

    private AmenityDTO mapAmenity(ResultSet resultSet) throws SQLException {
        return new AmenityDTO(
                resultSet.getString("amenity_id"),
                resultSet.getString("name"));
    }
}
