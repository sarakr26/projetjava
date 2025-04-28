package edu.sabanciuniv.hotelbookingapp.chain;

import edu.sabanciuniv.hotelbookingapp.model.Booking;

public class RoomAvailabilityValidator extends BookingValidator {
    @Override 
    public boolean validate(Booking booking) {
        // Add room availability validation logic
        return nextValidator != null ? nextValidator.validate(booking) : true;
    }
}
