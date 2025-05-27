package edu.sabanciuniv.hotelbookingapp.chain;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import edu.sabanciuniv.hotelbookingapp.chain.DateValidator;
import edu.sabanciuniv.hotelbookingapp.chain.RoomAvailabilityValidator;
import edu.sabanciuniv.hotelbookingapp.service.AvailabilityService;
import edu.sabanciuniv.hotelbookingapp.service.RoomService;
import lombok.RequiredArgsConstructor;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingValidatorFactory {
    
    private final AvailabilityService availabilityService;
    private final RoomService roomService;

    public BookingValidator createValidatorChain() {
        log.info("Creating booking validator chain");
        
        // Create the validators
        BookingValidator dateValidator = new DateValidator();
        BookingValidator roomAvailabilityValidator = new RoomAvailabilityValidator(availabilityService, roomService);
        
        // Set up the chain
        dateValidator.setNext(roomAvailabilityValidator);
        
        log.info("Booking validator chain created successfully");
        return dateValidator;
    }
} 