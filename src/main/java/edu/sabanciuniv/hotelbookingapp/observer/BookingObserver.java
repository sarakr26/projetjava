package edu.sabanciuniv.hotelbookingapp.observer;

import edu.sabanciuniv.hotelbookingapp.model.Booking;

public interface BookingObserver {
    void update(Booking booking);
}
