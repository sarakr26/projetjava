package edu.sabanciuniv.hotelbookingapp.model.enums;

public enum ServiceType {
    WIFI("Wi-Fi"),
    BREAKFAST("Breakfast"),
    PARKING("Parking"),
    SPA("Spa"),
    GYM("Gym"),
    POOL("Pool"),
    ROOM_SERVICE("Room Service");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
