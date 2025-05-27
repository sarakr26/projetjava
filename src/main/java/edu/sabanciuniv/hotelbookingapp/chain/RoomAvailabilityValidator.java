package edu.sabanciuniv.hotelbookingapp.chain;

import edu.sabanciuniv.hotelbookingapp.model.Booking;
import edu.sabanciuniv.hotelbookingapp.model.BookedRoom;
import edu.sabanciuniv.hotelbookingapp.service.AvailabilityService;
import edu.sabanciuniv.hotelbookingapp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.persistence.EntityNotFoundException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomAvailabilityValidator extends BookingValidator {
    
    private final AvailabilityService availabilityService;
    private final RoomService roomService;
    
    @Override 
    public boolean validate(Booking booking) {
        log.info("Validating room availability for booking ID: {}", booking.getId());
        
        try {
            for (BookedRoom bookedRoom : booking.getBookedRooms()) {
                // Find the Room entity using the roomType and Hotel from the Booking
                Long roomId = booking.getHotel().getRooms().stream()
                              .filter(room -> room.getRoomType() == bookedRoom.getRoomType())
                              .findFirst()
                              .orElseThrow(() -> new EntityNotFoundException("Room not found for type: " + bookedRoom.getRoomType()))
                              .getId();

                Integer availableRooms = availabilityService.getMinAvailableRooms(
                    roomId,
                    booking.getCheckinDate(),
                    booking.getCheckoutDate()
                );
                
                if (availableRooms < bookedRoom.getCount()) {
                    log.warn("Not enough rooms of type {} available. Required: {}, Available: {}", 
                            bookedRoom.getRoomType(), bookedRoom.getCount(), availableRooms);
                    return false;
                }
            }
            
            log.info("Room availability validation passed for booking ID: {}", booking.getId());
            return nextValidator != null ? nextValidator.validate(booking) : true;
        } catch (Exception e) {
            log.error("Error validating room availability: {}", e.getMessage());
            return false;
        }
    }
}
