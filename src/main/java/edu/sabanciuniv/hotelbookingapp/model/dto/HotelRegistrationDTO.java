package edu.sabanciuniv.hotelbookingapp.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HotelRegistrationDTO {
    @NotBlank(message = "Hotel name cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z0-9 ]+$", message = "Hotel name must only contain letters and numbers")
    private String name;

    @Valid
    private AddressDTO addressDTO;

    @Valid
    private List<RoomDTO> roomDTOs = new ArrayList<>();

    @Valid
    private List<HotelAmenityDTO> amenities = new ArrayList<>();
}
