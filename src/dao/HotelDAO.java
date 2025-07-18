package dao;

import entities.Hotel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {

    /* ---------- Create ---------- */
    public boolean addHotel(Hotel h) {
        String sql = "INSERT INTO hotels (name, location, amenities) VALUES (?, ?, ?)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, h.getName());
            ps.setString(2, h.getLocation());
            ps.setString(3, h.getAmenities());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) h.setHotelId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("addHotel: " + e.getMessage());
        }
        return false;
    }

    /* ---------- Read ---------- */
    public List<Hotel> getAllHotels() {
        List<Hotel> list = new ArrayList<>();
        String sql = "SELECT * FROM hotels ORDER BY name";
        try (Connection c = DatabaseConnector.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("getAllHotels: " + e.getMessage());
        }
        return list;
    }

    public Hotel getHotelById(int id) {
        String sql = "SELECT * FROM hotels WHERE hotel_id=?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("getHotelById: " + e.getMessage());
        }
        return null;
    }

    /* ---------- Update ---------- */
    public boolean updateHotel(Hotel h) {
        String sql = "UPDATE hotels SET name=?, location=?, amenities=? WHERE hotel_id=?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, h.getName());
            ps.setString(2, h.getLocation());
            ps.setString(3, h.getAmenities());
            ps.setInt   (4, h.getHotelId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateHotel: " + e.getMessage());
            return false;
        }
    }

    /* ---------- Delete ---------- */
    public boolean deleteHotel(int id) {
        String sql = "DELETE FROM hotels WHERE hotel_id=?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteHotel: " + e.getMessage());
            return false;
        }
    }

    /* ---------- Mapper ---------- */
    private Hotel map(ResultSet rs) throws SQLException {
        Hotel h = new Hotel();
        h.setHotelId   (rs.getInt("hotel_id"));
        h.setName      (rs.getString("name"));
        h.setLocation  (rs.getString("location"));
        h.setAmenities (rs.getString("amenities"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) h.setCreatedAt(ts.toLocalDateTime());
        return h;
    }
}
