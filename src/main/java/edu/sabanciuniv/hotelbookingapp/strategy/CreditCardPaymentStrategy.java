package edu.sabanciuniv.hotelbookingapp.strategy;

import edu.sabanciuniv.hotelbookingapp.model.Payment;

public class CreditCardPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(Payment payment) {
        // Add credit card payment processing logic
        return true;
    }
}
