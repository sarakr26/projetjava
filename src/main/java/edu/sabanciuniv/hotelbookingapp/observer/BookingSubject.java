package edu.sabanciuniv.hotelbookingapp.observer;

import edu.sabanciuniv.hotelbookingapp.model.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingSubject {
    private List<BookingObserver> observers = new ArrayList<>();
    
    public void attach(BookingObserver observer) {
        observers.add(observer);
    }
    
    public void detach(BookingObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(Booking booking) {
        for (BookingObserver observer : observers) {
            observer.update(booking);
        }
    }
}
