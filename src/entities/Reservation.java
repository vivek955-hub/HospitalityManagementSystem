package entities;

import java.sql.Timestamp;
import java.util.Date;

public class Reservation {

    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";
    public static final String COMPLETED = "COMPLETED";

    private int reservationId;
    private int guestId;
    private int roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private double totalPrice;
    private String status = CONFIRMED;  // Default to CONFIRMED
    private Timestamp createdAt;

    public Reservation() {}

    public Reservation(int guestId, int roomId, Date checkInDate,
                       Date checkOutDate, double totalPrice, String status) {
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getReservationId()       { return reservationId; }
    public void setReservationId(int id){ this.reservationId = id; }

    public int getGuestId()             { return guestId; }
    public void setGuestId(int id)      { this.guestId = id; }

    public int getRoomId()              { return roomId; }
    public void setRoomId(int id)       { this.roomId = id; }

    public Date getCheckInDate()        { return checkInDate; }
    public void setCheckInDate(Date d)  { this.checkInDate = d; }

    public Date getCheckOutDate()       { return checkOutDate; }
    public void setCheckOutDate(Date d) { this.checkOutDate = d; }

    public double getTotalPrice()       { return totalPrice; }
    public void setTotalPrice(double p) { this.totalPrice = p; }

    public String getStatus()           { return status; }
    public void setStatus(String s)     { this.status = s; }

    public Timestamp getCreatedAt()     { return createdAt; }
    public void setCreatedAt(Timestamp t) { this.createdAt = t; }

    public boolean isActive() {
        Date today = new Date();
        return CONFIRMED.equals(status) &&
               !today.before(checkInDate) &&
               !today.after(checkOutDate);
    }

    public long getDurationInDays() {
        if (checkInDate == null || checkOutDate == null) return 0;
        long diff = checkOutDate.getTime() - checkInDate.getTime();
        return diff / (1000 * 60 * 60 * 24);
    }

    @Override
    public String toString() {
        return String.format(
            "Reservation{id=%d, guest=%d, room=%d, %s to %s, ₹%.2f, status=%s}",
            reservationId, guestId, roomId,
            checkInDate, checkOutDate, totalPrice, status
        );
    }
}
