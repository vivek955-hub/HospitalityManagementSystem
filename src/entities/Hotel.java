package entities;

import java.time.LocalDateTime;

public class Hotel {
    private int hotelId;
    private String name;
    private String location;
    private String amenities;
    private LocalDateTime createdAt;

    // Constructors
    public Hotel() {}

    public Hotel(String name, String location, String amenities) {
        this.name = name;
        this.location = location;
        this.amenities = amenities;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString
    @Override
    public String toString() {
        return "Hotel{" +
                "hotelId=" + hotelId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", amenities='" + amenities + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
