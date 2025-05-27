package edu.sabanciuniv.hotelbookingapp.model.dto;

import edu.sabanciuniv.hotelbookingapp.model.enums.ServiceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelServiceDTO {
    
    private Long id;
    
    @NotNull(message = "Service type is required")
    private ServiceType serviceType;
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private Double pricePerDay;
    
    @NotNull(message = "Availability is required")
    private Boolean available;
}