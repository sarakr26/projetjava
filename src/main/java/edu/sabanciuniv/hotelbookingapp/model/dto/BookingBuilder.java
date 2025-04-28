package edu.sabanciuniv.hotelbookingapp.model.dto;

import edu.sabanciuniv.hotelbookingapp.model.Booking;
import java.time.LocalDate;
import java.util.List;

public class BookingBuilder {
    private Booking booking = new Booking();
    
    public BookingBuilder withDates(LocalDate checkin, LocalDate checkout) {
        booking.setCheckinDate(checkin);
        booking.setCheckoutDate(checkout); 
        return this;
    }
    
    public BookingBuilder withCustomer(Long customerId) {
        // Set customer
        return this;
    }
    
    public BookingBuilder withRooms(List<RoomDTO> rooms) {
        // Add rooms
        return this;  
    }
    
    public Booking build() {
        return booking;
    }
}
