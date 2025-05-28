package edu.sabanciuniv.hotelbookingapp.decorator;

import edu.sabanciuniv.hotelbookingapp.model.Room;
import edu.sabanciuniv.hotelbookingapp.model.enums.ServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class RoomDecoratorFactory {
    
    public Room createDecoratedRoom(Room baseRoom, Set<ServiceType> services) {
        log.info("Creating decorated room with services: {}", services);
        
        RoomDecorator decoratedRoom = null;
        
        for (ServiceType service : services) {
            switch (service) {
                case WIFI:
                    decoratedRoom = new WifiDecorator(decoratedRoom != null ? decoratedRoom : baseRoom);
                    break;
                case BREAKFAST:
                    decoratedRoom = new RoomServiceDecorators.BreakfastDecorator(decoratedRoom != null ? decoratedRoom : baseRoom);
                    break;
                case PARKING:
                    decoratedRoom = new RoomServiceDecorators.ParkingDecorator(decoratedRoom != null ? decoratedRoom : baseRoom);
                    break;
                case SPA:
                    decoratedRoom = new RoomServiceDecorators.SpaDecorator(decoratedRoom != null ? decoratedRoom : baseRoom);
                    break;
                case POOL:
                    decoratedRoom = new RoomServiceDecorators.PoolDecorator(decoratedRoom != null ? decoratedRoom : baseRoom);
                    break;
                case ROOM_SERVICE:
                    decoratedRoom = new RoomServiceDecorators.RoomServiceDecorator(decoratedRoom != null ? decoratedRoom : baseRoom);
                    break;
                default:
                    log.warn("Unknown service type: {}", service);
            }
        }
        
        return decoratedRoom != null ? decoratedRoom : baseRoom;
    }
} 