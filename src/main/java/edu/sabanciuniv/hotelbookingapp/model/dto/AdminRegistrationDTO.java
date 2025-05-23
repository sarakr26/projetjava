package edu.sabanciuniv.hotelbookingapp.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AdminRegistrationDTO extends UserRegistrationDTO {
    
    @NotEmpty(message = "Admin code is required")
    private String adminCode;
}
