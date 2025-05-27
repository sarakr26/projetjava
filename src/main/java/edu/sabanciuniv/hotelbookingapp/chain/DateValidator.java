package edu.sabanciuniv.hotelbookingapp.chain;

import edu.sabanciuniv.hotelbookingapp.model.Booking;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateValidator extends BookingValidator {
    @Override
    public boolean validate(Booking booking) {
        log.info("Validating booking dates for booking ID: {}", booking.getId());
        
        LocalDate now = LocalDate.now();
        if (booking.getCheckinDate().isBefore(now)) {
            log.warn("Invalid check-in date: {} is before current date", booking.getCheckinDate());
            return false;
        }
        
        if (booking.getCheckoutDate().isBefore(booking.getCheckinDate().plusDays(1))) {
            log.warn("Invalid check-out date: {} is not after check-in date", booking.getCheckoutDate());
            return false;
        }
        
        log.info("Date validation passed for booking ID: {}", booking.getId());
        return nextValidator != null ? nextValidator.validate(booking) : true;
    }
}
