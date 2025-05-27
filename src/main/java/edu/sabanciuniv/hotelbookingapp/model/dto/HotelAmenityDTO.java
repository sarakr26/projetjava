package edu.sabanciuniv.hotelbookingapp.model.dto;

import edu.sabanciuniv.hotelbookingapp.model.enums.ServiceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAmenityDTO {
    private Long id;
    
    @NotNull(message = "Service type is required")
    private ServiceType serviceType;
    
    @Min(value = 0, message = "Price must be positive")
    private double pricePerDay;
    
    private boolean available;
}
