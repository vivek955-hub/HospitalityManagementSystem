package dao;

import entities.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public boolean addGuest(Guest guest) {
        String sql = "INSERT INTO guests (name, email, phone) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getEmail());
            stmt.setString(3, guest.getPhone());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) guest.setGuestId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("addGuest: " + e.getMessage());
        }
        return false;
    }

    public List<Guest> getAllGuests() {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("getAllGuests: " + e.getMessage());
        }
        return list;
    }

    public boolean updateGuest(Guest guest) {
        String sql = "UPDATE guests SET name=?, email=?, phone=? WHERE guest_id=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getEmail());
            stmt.setString(3, guest.getPhone());
            stmt.setInt   (4, guest.getGuestId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateGuest: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteGuest(int id) {
        String sql = "DELETE FROM guests WHERE guest_id=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteGuest: " + e.getMessage());
            return false;
        }
    }

    public Guest getGuestById(int id) {
        String sql = "SELECT * FROM guests WHERE guest_id=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("getGuestById: " + e.getMessage());
        }
        return null;
    }

    /* helper */
    private Guest map(ResultSet rs) throws SQLException {
        Guest g = new Guest();
        g.setGuestId(rs.getInt("guest_id"));
        g.setName   (rs.getString("name"));
        g.setEmail  (rs.getString("email"));
        g.setPhone  (rs.getString("phone"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) g.setCreatedAt(ts.toLocalDateTime());
        return g;
    }
}
