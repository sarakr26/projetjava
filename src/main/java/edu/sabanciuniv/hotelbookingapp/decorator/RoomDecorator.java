package edu.sabanciuniv.hotelbookingapp.decorator;

import edu.sabanciuniv.hotelbookingapp.model.Room;

public abstract class RoomDecorator extends Room {
    protected Room decoratedRoom;
    
    public RoomDecorator(Room room) {
        this.decoratedRoom = room;
    }
    
    public abstract double getPrice();
}

class WifiDecorator extends RoomDecorator {
    public WifiDecorator(Room room) {
        super(room);
    }
    
    @Override
    public double getPrice() {
        return decoratedRoom.getPricePerNight() + 5.0; // Add WiFi cost
    }
}
