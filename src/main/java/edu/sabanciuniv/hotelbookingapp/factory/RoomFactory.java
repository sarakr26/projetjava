package edu.sabanciuniv.hotelbookingapp.factory;

import edu.sabanciuniv.hotelbookingapp.model.Room;
import edu.sabanciuniv.hotelbookingapp.model.enums.RoomType;

public interface RoomFactory {
    Room createRoom(Double price, Integer count);
}

class SingleRoomFactory implements RoomFactory {
    @Override
    public Room createRoom(Double price, Integer count) {
        return Room.builder()
                .roomType(RoomType.SINGLE)
                .pricePerNight(price)
                .roomCount(count)
                .build();
    }
}

class DoubleRoomFactory implements RoomFactory {
    @Override
    public Room createRoom(Double price, Integer count) {
        return Room.builder()
                .roomType(RoomType.DOUBLE)
                .pricePerNight(price)
                .roomCount(count)
                .build();
    }
}
