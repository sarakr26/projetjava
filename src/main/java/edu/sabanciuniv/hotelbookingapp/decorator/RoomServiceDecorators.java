package edu.sabanciuniv.hotelbookingapp.decorator;

import edu.sabanciuniv.hotelbookingapp.model.Room;

public class RoomServiceDecorators {
    
    // Breakfast Service Decorator
    public static class BreakfastDecorator extends RoomDecorator {
        private static final double BREAKFAST_PRICE = 15.0;
        
        public BreakfastDecorator(Room room) {
            super(room);
        }
        
        @Override
        public double getPrice() {
            return decoratedRoom.getPricePerNight() + BREAKFAST_PRICE;
        }
    }
    
    // Parking Service Decorator
    public static class ParkingDecorator extends RoomDecorator {
        private static final double PARKING_PRICE = 10.0;
        
        public ParkingDecorator(Room room) {
            super(room);
        }
        
        @Override
        public double getPrice() {
            return decoratedRoom.getPricePerNight() + PARKING_PRICE;
        }
    }
    
    // Spa Service Decorator
    public static class SpaDecorator extends RoomDecorator {
        private static final double SPA_PRICE = 25.0;
        
        public SpaDecorator(Room room) {
            super(room);
        }
        
        @Override
        public double getPrice() {
            return decoratedRoom.getPricePerNight() + SPA_PRICE;
        }
    }
    
    // Pool Access Decorator
    public static class PoolDecorator extends RoomDecorator {
        private static final double POOL_PRICE = 8.0;
        
        public PoolDecorator(Room room) {
            super(room);
        }
        
        @Override
        public double getPrice() {
            return decoratedRoom.getPricePerNight() + POOL_PRICE;
        }
    }
    
    // Room Service Decorator
    public static class RoomServiceDecorator extends RoomDecorator {
        private static final double ROOM_SERVICE_PRICE = 20.0;
        
        public RoomServiceDecorator(Room room) {
            super(room);
        }
        
        @Override
        public double getPrice() {
            return decoratedRoom.getPricePerNight() + ROOM_SERVICE_PRICE;
        }
    }
} 