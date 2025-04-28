package edu.sabanciuniv.hotelbookingapp.strategy;

import edu.sabanciuniv.hotelbookingapp.model.Payment;

public interface PaymentStrategy {
    boolean processPayment(Payment payment);
}
