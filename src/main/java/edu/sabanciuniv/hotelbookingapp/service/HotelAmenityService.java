package edu.sabanciuniv.hotelbookingapp.service;

import edu.sabanciuniv.hotelbookingapp.model.HotelAmenity;
import edu.sabanciuniv.hotelbookingapp.model.dto.HotelAmenityDTO;
import java.util.List;

public interface HotelAmenityService {
    List<HotelAmenity> saveAmenities(List<HotelAmenityDTO> amenityDTOs, Long hotelId);
    List<HotelAmenityDTO> findByHotelId(Long hotelId);
}
