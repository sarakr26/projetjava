package edu.sabanciuniv.hotelbookingapp.chain;

import edu.sabanciuniv.hotelbookingapp.model.Booking;

public abstract class BookingValidator {
    protected BookingValidator nextValidator;
    
    public void setNext(BookingValidator validator) {
        this.nextValidator = validator;
    }
    
    public abstract boolean validate(Booking booking);
}
