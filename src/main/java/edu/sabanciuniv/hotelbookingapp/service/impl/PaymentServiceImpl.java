package edu.sabanciuniv.hotelbookingapp.service.impl;

import edu.sabanciuniv.hotelbookingapp.model.Booking;
import edu.sabanciuniv.hotelbookingapp.model.Payment;
import edu.sabanciuniv.hotelbookingapp.model.dto.BookingInitiationDTO;
import edu.sabanciuniv.hotelbookingapp.model.enums.Currency;
import edu.sabanciuniv.hotelbookingapp.model.enums.PaymentMethod;
import edu.sabanciuniv.hotelbookingapp.model.enums.PaymentStatus;
import edu.sabanciuniv.hotelbookingapp.repository.PaymentRepository;
import edu.sabanciuniv.hotelbookingapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment savePayment(BookingInitiationDTO bookingInitiationDTO, Booking booking) {
        try {
            Payment payment = Payment.builder()
                    .booking(booking)
                    .totalPrice(new BigDecimal(String.valueOf(bookingInitiationDTO.getTotalPrice())))
                    .paymentStatus(PaymentStatus.COMPLETED) // Assuming the payment is completed
                    .paymentMethod(PaymentMethod.CREDIT_CARD) // Default to CREDIT_CARD
                    .currency(Currency.USD) // Default to USD
                    .build();

            payment = paymentRepository.saveAndFlush(payment); // Use saveAndFlush to ensure immediate persistence
            booking.setPayment(payment);
            log.info("Payment saved successfully with ID: {} for booking: {}", payment.getId(), booking.getId());
            return payment;
        } catch (Exception e) {
            log.error("Error saving payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process payment", e);
        }
    }
}
