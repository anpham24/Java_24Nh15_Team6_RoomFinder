package DAL;

import DTOs.RoomImageDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoomImageDAL {
    public List<RoomImageDTO> findByRoomId(String roomId) throws SQLException {
        String sql = "SELECT image_id, room_id, image_path FROM room_images WHERE room_id = ? ORDER BY image_id";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapImages(resultSet);
            }
        }
    }

    public int insert(RoomImageDTO image) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return insert(connection, image);
        }
    }

    public int insert(Connection connection, RoomImageDTO image) throws SQLException {
        String sql = "INSERT INTO room_images(room_id, image_path) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, image.getRoomId());
            statement.setString(2, image.getImagePath());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    image.setImageId(id);
                    return id;
                }
            }
        }
        return 0;
    }

    public void insertBatch(Connection connection, String roomId, List<RoomImageDTO> images) throws SQLException {
        if (images == null || images.isEmpty()) {
            return;
        }

        for (RoomImageDTO image : images) {
            image.setRoomId(roomId);
            insert(connection, image);
        }
    }

    public boolean delete(int imageId) throws SQLException {
        String sql = "DELETE FROM room_images WHERE image_id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, imageId);
            return statement.executeUpdate() > 0;
        }
    }

    public int deleteByRoomId(Connection connection, String roomId) throws SQLException {
        String sql = "DELETE FROM room_images WHERE room_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            return statement.executeUpdate();
        }
    }

    public void replaceForRoom(Connection connection, String roomId, List<RoomImageDTO> images) throws SQLException {
        deleteByRoomId(connection, roomId);
        insertBatch(connection, roomId, images);
    }

    private List<RoomImageDTO> mapImages(ResultSet resultSet) throws SQLException {
        List<RoomImageDTO> images = new ArrayList<>();
        while (resultSet.next()) {
            images.add(new RoomImageDTO(
                    resultSet.getInt("image_id"),
                    resultSet.getString("room_id"),
                    resultSet.getString("image_path")));
        }
        return images;
    }
}
