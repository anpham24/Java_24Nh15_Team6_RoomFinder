package DAL;

import DTOs.AmenityDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AmenityDAL {
    public List<AmenityDTO> findAll() throws SQLException {
        String sql = "SELECT amenity_id, name FROM amenities ORDER BY name";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            return mapAmenities(resultSet);
        }
    }

    public AmenityDTO findById(int amenityId) throws SQLException {
        String sql = "SELECT amenity_id, name FROM amenities WHERE amenity_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amenityId);
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

    public List<AmenityDTO> findByRoomId(int roomId) throws SQLException {
        String sql = "SELECT a.amenity_id, a.name "
                + "FROM amenities a "
                + "INNER JOIN room_amenities ra ON ra.amenity_id = a.amenity_id "
                + "WHERE ra.room_id = ? "
                + "ORDER BY a.name";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapAmenities(resultSet);
            }
        }
    }

    public int insert(AmenityDTO amenity) throws SQLException {
        String sql = "INSERT INTO amenities(name) VALUES (?)";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, amenity.getName());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    amenity.setAmenityId(id);
                    return id;
                }
            }
        }
        return 0;
    }

    public boolean update(AmenityDTO amenity) throws SQLException {
        String sql = "UPDATE amenities SET name = ? WHERE amenity_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, amenity.getName());
            statement.setInt(2, amenity.getAmenityId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int amenityId) throws SQLException {
        String sql = "DELETE FROM amenities WHERE amenity_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amenityId);
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
                resultSet.getInt("amenity_id"),
                resultSet.getString("name"));
    }
}
