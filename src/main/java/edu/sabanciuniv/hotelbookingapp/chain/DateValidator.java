package edu.sabanciuniv.hotelbookingapp.chain;

import edu.sabanciuniv.hotelbookingapp.model.Booking;
import java.time.LocalDate;

public class DateValidator extends BookingValidator {
    @Override
    public boolean validate(Booking booking) {
        LocalDate now = LocalDate.now();
        if (booking.getCheckinDate().isBefore(now)) {
            return false;
        }
        if (booking.getCheckoutDate().isBefore(booking.getCheckinDate().plusDays(1))) {
            return false;
        }
        return nextValidator != null ? nextValidator.validate(booking) : true;
    }
}
