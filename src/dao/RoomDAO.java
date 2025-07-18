package dao;

import entities.Room;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public boolean addRoom(Room r) {
        String sql = "INSERT INTO rooms (hotel_id, room_number, type, price, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt   (1, r.getHotelId());
            ps.setString(2, r.getRoomNumber());
            ps.setString(3, r.getType());
            ps.setDouble(4, r.getPrice());
            ps.setString(5, r.getStatus());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) r.setRoomId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("addRoom: " + e.getMessage());
        }
        return false;
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Connection c = DatabaseConnector.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("getAllRooms: " + e.getMessage());
        }
        return list;
    }

    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE room_id=?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("getRoomById: " + e.getMessage());
        }
        return null;
    }

    public boolean updateRoom(Room r) {
        String sql = "UPDATE rooms SET hotel_id=?, room_number=?, type=?, price=?, status=? WHERE room_id=?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt   (1, r.getHotelId());
            ps.setString(2, r.getRoomNumber());
            ps.setString(3, r.getType());
            ps.setDouble(4, r.getPrice());
            ps.setString(5, r.getStatus());
            ps.setInt   (6, r.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateRoom: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE room_id=?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteRoom: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Room> list = new ArrayList<>();
        String sql =
            "SELECT r.* FROM rooms r " +
            "WHERE r.status = 'AVAILABLE' " +
            "  AND r.room_id NOT IN (" +
            "      SELECT res.room_id FROM reservations res " +
            "      WHERE res.status = 'CONFIRMED' " +
            "        AND res.check_out_date > ? " +
            "        AND res.check_in_date < ?)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(checkIn));
            ps.setDate(2, Date.valueOf(checkOut));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("getAvailableRooms: " + e.getMessage());
        }
        return list;
    }

    /* helper */
    private Room map(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId     (rs.getInt("room_id"));
        r.setHotelId    (rs.getInt("hotel_id"));
        r.setRoomNumber (rs.getString("room_number"));
        r.setType       (rs.getString("type"));
        r.setPrice      (rs.getDouble("price"));
        r.setStatus     (rs.getString("status"));
        return r;
    }
}
