package edu.sabanciuniv.hotelbookingapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityDTO {

    private Long id;

    private String name;

    private AddressDTO addressDTO;

    @Builder.Default
    private List<RoomDTO> roomDTOs = new ArrayList<>();

    private Integer maxAvailableSingleRooms;

    private Integer maxAvailableDoubleRooms;

    @Builder.Default
    private List<HotelAmenityDTO> amenities = new ArrayList<>();

}
