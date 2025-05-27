package edu.sabanciuniv.hotelbookingapp.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PayPalPaymentDTO {
    
    @NotEmpty(message = "PayPal email is required")
    @Email(message = "Invalid PayPal email address")
    private String paypalEmail;
    
    // Additional PayPal-specific fields can be added here
    // such as shipping address, currency preference, etc.
} 