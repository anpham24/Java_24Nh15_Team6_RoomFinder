package DAL;

import DTOs.RoomDTO;
import DTOs.RoomSearchCriteriaDTO;
import DTOs.RoomSearchCriteriaDTO.SortBy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoomDAL {
    public RoomDTO findById(String roomId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return findById(connection, roomId);
        }
    }

    public RoomDTO findById(Connection connection, String roomId) throws SQLException {
        String sql = baseSelect() + " WHERE r.room_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRoom(resultSet);
                }
            }
        }
        return null;
    }

    public List<RoomDTO> findAll() throws SQLException {
        return search(new RoomSearchCriteriaDTO());
    }

    public List<RoomDTO> findPending() throws SQLException {
        RoomSearchCriteriaDTO criteria = new RoomSearchCriteriaDTO();
        criteria.setStatus(Boolean.FALSE);
        criteria.setSortBy(SortBy.CREATED_AT_DESC);
        return search(criteria);
    }

    public List<RoomDTO> findApprovedAvailable() throws SQLException {
        RoomSearchCriteriaDTO criteria = new RoomSearchCriteriaDTO();
        criteria.setStatus(Boolean.TRUE);
        criteria.setAvailability(Boolean.TRUE);
        criteria.setSortBy(SortBy.CREATED_AT_DESC);
        return search(criteria);
    }

    public List<RoomDTO> findByLandlordId(String landlordId) throws SQLException {
        RoomSearchCriteriaDTO criteria = new RoomSearchCriteriaDTO();
        criteria.setLandlordId(landlordId);
        criteria.setSortBy(SortBy.CREATED_AT_DESC);
        return search(criteria);
    }

    public List<RoomDTO> search(RoomSearchCriteriaDTO criteria) throws SQLException {
        if (criteria == null) {
            criteria = new RoomSearchCriteriaDTO();
        }

        StringBuilder sql = new StringBuilder(baseSelect()).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
            sql.append(" AND (LOWER(r.title) LIKE ? OR LOWER(r.address) LIKE ? OR LOWER(r.description) LIKE ?)");
            String pattern = "%" + criteria.getKeyword().trim().toLowerCase() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (criteria.getMinPrice() != null) {
            sql.append(" AND r.price >= ?");
            params.add(criteria.getMinPrice());
        }

        if (criteria.getMaxPrice() != null) {
            sql.append(" AND r.price <= ?");
            params.add(criteria.getMaxPrice());
        }

        if (criteria.getStatus() != null) {
            sql.append(" AND r.status = ?");
            params.add(criteria.getStatus());
        }

        if (criteria.getAvailability() != null) {
            sql.append(" AND r.availability = ?");
            params.add(criteria.getAvailability());
        }

        if (criteria.getLandlordId() != null) {
            sql.append(" AND r.landlord_id = ?");
            params.add(criteria.getLandlordId());
        }

        if (criteria.hasAmenityFilter()) {
            for (String amenityId : criteria.getAmenityIds()) {
                if (amenityId != null) {
                    sql.append(" AND EXISTS (SELECT 1 FROM room_amenities ra_filter ")
                            .append("WHERE ra_filter.room_id = r.room_id AND ra_filter.amenity_id = ?)");
                    params.add(amenityId);
                }
            }
        }

        sql.append(orderBy(criteria.getSortBy()));

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            bindParams(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapRooms(resultSet);
            }
        }
    }

    public String insert(RoomDTO room) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return insert(connection, room);
        }
    }

    public String insert(Connection connection, RoomDTO room) throws SQLException {
        String roomId = UUID.randomUUID().toString();
        String sql = "INSERT INTO rooms(room_id, landlord_id, title, address, description, area, price, status, availability, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            statement.setString(2, room.getLandlordId());
            statement.setString(3, room.getTitle());
            statement.setString(4, room.getAddress());
            statement.setString(5, room.getDescription());
            statement.setDouble(6, room.getArea());
            statement.setDouble(7, room.getPrice());
            statement.setBoolean(8, room.isStatus());
            statement.setBoolean(9, room.isAvailability());
            statement.executeUpdate();
            room.setRoomId(roomId);
            return roomId;
        }
    }

    public boolean update(RoomDTO room) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return update(connection, room);
        }
    }

    public boolean update(Connection connection, RoomDTO room) throws SQLException {
        String sql = "UPDATE rooms SET title = ?, address = ?, description = ?, area = ?, price = ?, "
                + "status = ?, availability = ? WHERE room_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, room.getTitle());
            statement.setString(2, room.getAddress());
            statement.setString(3, room.getDescription());
            statement.setDouble(4, room.getArea());
            statement.setDouble(5, room.getPrice());
            statement.setBoolean(6, room.isStatus());
            statement.setBoolean(7, room.isAvailability());
            statement.setString(8, room.getRoomId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(String roomId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return delete(connection, roomId);
        }
    }

    public boolean delete(Connection connection, String roomId) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean approve(String roomId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return approve(connection, roomId);
        }
    }

    public boolean approve(Connection connection, String roomId) throws SQLException {
        String sql = "UPDATE rooms SET status = TRUE WHERE room_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateAvailability(String roomId, boolean availability) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return updateAvailability(connection, roomId, availability);
        }
    }

    public boolean updateAvailability(Connection connection, String roomId, boolean availability) throws SQLException {
        String sql = "UPDATE rooms SET availability = ? WHERE room_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, availability);
            statement.setString(2, roomId);
            return statement.executeUpdate() > 0;
        }
    }

    private String baseSelect() {
        return "SELECT r.room_id, r.landlord_id, r.title, r.address, r.description, r.area, r.price, "
                + "r.status, r.availability, r.created_at, "
                + "COALESCE(rv.average_rating, 0) AS average_rating, "
                + "COALESCE(rv.review_count, 0) AS review_count "
                + "FROM rooms r "
                + "LEFT JOIN ("
                + "SELECT room_id, AVG(rating) AS average_rating, COUNT(*) AS review_count "
                + "FROM reviews GROUP BY room_id"
                + ") rv ON rv.room_id = r.room_id";
    }

    private String orderBy(SortBy sortBy) {
        if (sortBy == null) {
            sortBy = SortBy.NONE;
        }

        switch (sortBy) {
            case PRICE_ASC:
                return " ORDER BY r.price ASC, r.created_at DESC";
            case PRICE_DESC:
                return " ORDER BY r.price DESC, r.created_at DESC";
            case RATING_DESC:
                return " ORDER BY average_rating DESC, review_count DESC, r.created_at DESC";
            case AREA_ASC:
                return " ORDER BY r.area ASC, r.created_at DESC";
            case AREA_DESC:
                return " ORDER BY r.area DESC, r.created_at DESC";
            case CREATED_AT_DESC:
            case NONE:
            default:
                return " ORDER BY r.created_at DESC, r.room_id DESC";
        }
    }

    private void bindParams(PreparedStatement statement, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof Boolean) {
                statement.setBoolean(i + 1, (Boolean) param);
            } else {
                statement.setObject(i + 1, param);
            }
        }
    }

    private List<RoomDTO> mapRooms(ResultSet resultSet) throws SQLException {
        List<RoomDTO> rooms = new ArrayList<>();
        while (resultSet.next()) {
            rooms.add(mapRoom(resultSet));
        }
        return rooms;
    }

    private RoomDTO mapRoom(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        RoomDTO room = new RoomDTO(
                resultSet.getString("room_id"),
                resultSet.getString("landlord_id"),
                resultSet.getString("title"),
                resultSet.getString("address"),
                resultSet.getString("description"),
                resultSet.getDouble("area"),
                resultSet.getDouble("price"),
                resultSet.getBoolean("status"),
                resultSet.getBoolean("availability"),
                createdAt == null ? null : createdAt.toLocalDateTime());

        if (hasColumn(resultSet, "average_rating")) {
            room.setAverageRating(resultSet.getDouble("average_rating"));
        }

        if (hasColumn(resultSet, "review_count")) {
            room.setReviewCount(resultSet.getInt("review_count"));
        }

        return room;
    }

    private boolean hasColumn(ResultSet resultSet, String columnName) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))) {
                return true;
            }
        }
        return false;
    }
}
