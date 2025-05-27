package edu.sabanciuniv.hotelbookingapp.strategy;

import edu.sabanciuniv.hotelbookingapp.model.Payment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DebitCardPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(Payment payment) {
        try {
            // Add debit card payment processing logic
            log.info("Processing debit card payment for transaction: {}", payment.getTransactionId());
            // Here you would typically:
            // 1. Validate the debit card details
            // 2. Check if the account has sufficient funds
            // 3. Process the payment through a payment gateway
            // 4. Handle the response
            
            return true;
        } catch (Exception e) {
            log.error("Error processing debit card payment: {}", e.getMessage());
            return false;
        }
    }
}
