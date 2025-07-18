package dao;

import entities.Reservation;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReservationDAO {

    public boolean addReservation(Reservation r) {
        String sql = "INSERT INTO reservations (guest_id, room_id, check_in_date, check_out_date, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getGuestId());
            ps.setInt(2, r.getRoomId());
            ps.setDate(3, new java.sql.Date(r.getCheckInDate().getTime()));
            ps.setDate(4, new java.sql.Date(r.getCheckOutDate().getTime()));
            ps.setDouble(5, r.getTotalPrice());
            ps.setString(6, r.getStatus());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) r.setReservationId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Add error: " + e.getMessage());
        }
        return false;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY check_in_date DESC";
        try (Connection c = DatabaseConnector.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Fetch error: " + e.getMessage());
        }
        return list;
    }

    public Reservation getReservationById(int id) {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("Get by ID error: " + e.getMessage());
        }
        return null;
    }

    public boolean updateReservation(Reservation r) {
        String sql = "UPDATE reservations SET guest_id=?, room_id=?, check_in_date=?, check_out_date=?, total_price=?, status=? WHERE reservation_id=?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, r.getGuestId());
            ps.setInt(2, r.getRoomId());
            ps.setDate(3, new java.sql.Date(r.getCheckInDate().getTime()));
            ps.setDate(4, new java.sql.Date(r.getCheckOutDate().getTime()));
            ps.setDouble(5, r.getTotalPrice());
            ps.setString(6, r.getStatus());
            ps.setInt(7, r.getReservationId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update error: " + e.getMessage());
        }
        return false;
    }

    public boolean cancelReservation(int id) {
        String sql = "UPDATE reservations SET status='CANCELLED' WHERE reservation_id=?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Cancel error: " + e.getMessage());
        }
        return false;
    }

    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE room_id=? AND status='CONFIRMED' " +
                     "AND check_out_date > ? AND check_in_date < ?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setDate(2, java.sql.Date.valueOf(checkIn));
            ps.setDate(3, java.sql.Date.valueOf(checkOut));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Availability error: " + e.getMessage());
        }
        return false;
    }

    /* -------- Helper -------- */
    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setGuestId(rs.getInt("guest_id"));
        r.setRoomId(rs.getInt("room_id"));
        r.setCheckInDate(rs.getDate("check_in_date"));
        r.setCheckOutDate(rs.getDate("check_out_date"));
        r.setTotalPrice(rs.getDouble("total_price"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}
