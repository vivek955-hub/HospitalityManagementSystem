package entities;

/** Very simple domain model for a hotel room. */
public class Room {

    /* -------- Status constants -------- */
    public static final String AVAILABLE   = "AVAILABLE";
    public static final String OCCUPIED    = "OCCUPIED";
    public static final String MAINTENANCE = "MAINTENANCE";

    /* -------- Type examples -------- */
    public static final String STANDARD = "STANDARD";
    public static final String DELUXE   = "DELUXE";
    public static final String SUITE    = "SUITE";

    /* -------- Fields -------- */
    private int    roomId;
    private int    hotelId;
    private String roomNumber;
    private String type;
    private double price;
    private String status = AVAILABLE;   // default

    /* -------- Constructors -------- */
    public Room() { }

    public Room(int hotelId, String roomNumber, String type, double price, String status) {
        this.hotelId    = hotelId;
        this.roomNumber = roomNumber;
        this.type       = type;
        this.price      = price;
        this.status     = status;
    }

    /** “No rooms available” placeholder (never saved to DB). */
    public static Room placeholder(String text) {
        Room p = new Room();
        p.roomId     = -1;
        p.hotelId    = -1;
        p.roomNumber = text;
        p.type       = "N/A";
        p.price      = 0.0;
        p.status     = AVAILABLE;
        return p;
    }

    /* -------- Getters / Setters -------- */
    public int getRoomId()          { return roomId; }
    public void setRoomId(int id)   { this.roomId = id; }

    public int getHotelId()         { return hotelId; }
    public void setHotelId(int id)  { this.hotelId = id; }

    public String getRoomNumber()   { return roomNumber; }
    public void setRoomNumber(String num) { this.roomNumber = num; }

    public String getType()         { return type; }
    public void setType(String type){ this.type = type; }

    public double getPrice()        { return price; }
    public void setPrice(double p)  { this.price = p; }

    public String getStatus()       { return status; }
    public void setStatus(String s) { this.status = s; }

    /* -------- String representation -------- */
    @Override
    public String toString() {
        // For the placeholder, just show its custom text
        if (roomId == -1) return roomNumber;
        return roomNumber + " (" + type + ")";
    }
}
