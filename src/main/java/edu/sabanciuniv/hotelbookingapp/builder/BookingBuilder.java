package edu.sabanciuniv.hotelbookingapp.builder;

import edu.sabanciuniv.hotelbookingapp.model.Booking;
import edu.sabanciuniv.hotelbookingapp.model.BookedRoom;
import edu.sabanciuniv.hotelbookingapp.model.dto.RoomSelectionDTO;
import java.time.LocalDate;
import java.util.List;

public class BookingBuilder {
    private final Booking booking = new Booking();
    
    public BookingBuilder withDates(LocalDate checkin, LocalDate checkout) {
        booking.setCheckinDate(checkin);
        booking.setCheckoutDate(checkout);
        return this;
    }
    
    public BookingBuilder withCustomer(Long customerId) {
        // Note: Customer needs to be set by service layer
        return this;
    }
    
    public BookingBuilder withRooms(List<RoomSelectionDTO> roomSelections) {
        roomSelections.forEach(selection -> {
            if (selection.getCount() > 0) {
                BookedRoom bookedRoom = BookedRoom.builder()
                    .booking(booking)
                    .roomType(selection.getRoomType())
                    .count(selection.getCount())
                    .build();
                booking.getBookedRooms().add(bookedRoom);
            }
        });
        return this;
    }
    
    public Booking build() {
        return booking;
    }
}
