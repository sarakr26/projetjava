package edu.sabanciuniv.hotelbookingapp.service.impl;

import edu.sabanciuniv.hotelbookingapp.model.Hotel;
import edu.sabanciuniv.hotelbookingapp.model.enums.RoomType;
import edu.sabanciuniv.hotelbookingapp.model.dto.AddressDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.HotelAvailabilityDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.HotelAmenityDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.RoomDTO;
import edu.sabanciuniv.hotelbookingapp.repository.HotelRepository;
import edu.sabanciuniv.hotelbookingapp.service.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import edu.sabanciuniv.hotelbookingapp.model.Booking;
import edu.sabanciuniv.hotelbookingapp.chain.BookingValidator;
import edu.sabanciuniv.hotelbookingapp.chain.BookingValidatorFactory;
import edu.sabanciuniv.hotelbookingapp.model.Photo;
import edu.sabanciuniv.hotelbookingapp.repository.PhotoRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelSearchServiceImpl implements HotelSearchService {

    private final HotelRepository hotelRepository;
    private final AddressService addressService;
    private final RoomService roomService;
    private final AvailabilityService availabilityService;
    private final BookingValidatorFactory validatorFactory;
    private final PhotoRepository photoRepository;

    @Override
    public List<HotelAvailabilityDTO> findAvailableHotelsByCityAndDate(String city, LocalDate checkinDate, LocalDate checkoutDate) {
        // Create a temporary booking for validation
        Booking tempBooking = Booking.builder()
            .checkinDate(checkinDate)
            .checkoutDate(checkoutDate)
            .build();
            
        // Validate using the chain
        BookingValidator validator = validatorFactory.createValidatorChain();
        if (!validator.validate(tempBooking)) {
            throw new IllegalArgumentException("Invalid booking dates");
        }

        log.info("Attempting to find hotels in {} with available rooms from {} to {}", 
                city, checkinDate, checkoutDate);

        // Number of days between check-in and check-out
        Long numberOfDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);

        // 1. Fetch hotels that satisfy the criteria (min 1 available room throughout the booking range)
        List<Hotel> hotelsWithAvailableRooms = hotelRepository.findHotelsWithAvailableRooms(city, checkinDate, checkoutDate, numberOfDays);

        // 2. Fetch hotels that don't have any availability records for the entire booking range
        List<Hotel> hotelsWithoutAvailabilityRecords = hotelRepository.findHotelsWithoutAvailabilityRecords(city, checkinDate, checkoutDate);

        // 3. Fetch hotels with partial availability; some days with records meeting the criteria and some days without any records
        List<Hotel> hotelsWithPartialAvailabilityRecords = hotelRepository.findHotelsWithPartialAvailabilityRecords(city, checkinDate, checkoutDate, numberOfDays);

        // Combine and deduplicate the hotels using a Set
        Set<Hotel> combinedHotels = new HashSet<>(hotelsWithAvailableRooms);
        combinedHotels.addAll(hotelsWithoutAvailabilityRecords);
        combinedHotels.addAll(hotelsWithPartialAvailabilityRecords);

        log.info("Successfully found {} hotels with available rooms", combinedHotels.size());

        // Convert the combined hotel list to DTOs for the response
        return combinedHotels.stream()
                .map(hotel -> mapHotelToHotelAvailabilityDto(hotel, checkinDate, checkoutDate))
                .collect(Collectors.toList());
    }

    @Override
    public HotelAvailabilityDTO findAvailableHotelById(Long hotelId, LocalDate checkinDate, LocalDate checkoutDate) {
        // Create a temporary booking for validation
        Booking tempBooking = Booking.builder()
            .checkinDate(checkinDate)
            .checkoutDate(checkoutDate)
            .build();
            
        // Validate using the chain
        BookingValidator validator = validatorFactory.createValidatorChain();
        if (!validator.validate(tempBooking)) {
            throw new IllegalArgumentException("Invalid booking dates");
        }

        log.info("Attempting to find hotel with ID {} with available rooms from {} to {}", hotelId, checkinDate, checkoutDate);

        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            log.error("No hotel found with ID: {}", hotelId);
            throw new EntityNotFoundException("Hotel not found");
        }

        Hotel hotel = hotelOptional.get();
        return mapHotelToHotelAvailabilityDto(hotel, checkinDate, checkoutDate);
    }


    @Override
    public HotelAvailabilityDTO mapHotelToHotelAvailabilityDto(Hotel hotel, LocalDate checkinDate, LocalDate checkoutDate) {
        log.debug("Mapping hotel with ID {} to HotelAvailabilityDTO", hotel.getId());
        List<RoomDTO> roomDTOs = hotel.getRooms().stream()
                .map(roomService::mapRoomToRoomDto)  // convert each Room to RoomDTO
                .collect(Collectors.toList());

        AddressDTO addressDTO = addressService.mapAddressToAddressDto(hotel.getAddress());
        
        // Get all photo URLs for the hotel
        List<Photo> hotelPhotos = photoRepository.findByHotelId(hotel.getId());
        List<String> photos = hotelPhotos.stream()
                .map(Photo::getPhotoUrl)
                .collect(Collectors.toList());
        
        // For backward compatibility
        String photoUrl = photos.isEmpty() ? null : photos.get(0);
        
        log.debug("Hotel ID: {}, Photos found: {}", hotel.getId(), photos.size());
        
        HotelAvailabilityDTO hotelAvailabilityDTO = HotelAvailabilityDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .addressDTO(addressDTO)
                .roomDTOs(roomDTOs)
                .photos(photos)
                .photoUrl(photoUrl)
                .amenities(hotel.getAmenities().stream()
                    .map(amenity -> HotelAmenityDTO.builder()
                        .id(amenity.getId())
                        .serviceType(amenity.getServiceType())
                        .pricePerDay(amenity.getPricePerDay())
                        .available(amenity.isAvailable())
                        .build())
                    .collect(Collectors.toList()))
                .build();
        
        // For each room type, find the minimum available rooms across the date range
        int maxAvailableSingleRooms = hotel.getRooms().stream()
                .filter(room -> room.getRoomType() == RoomType.SINGLE)
                .mapToInt(room -> availabilityService.getMinAvailableRooms(room.getId(), checkinDate, checkoutDate))
                .max()
                .orElse(0); // Assume no single rooms if none match the filter
        hotelAvailabilityDTO.setMaxAvailableSingleRooms(maxAvailableSingleRooms);

        int maxAvailableDoubleRooms = hotel.getRooms().stream()
                .filter(room -> room.getRoomType() == RoomType.DOUBLE)
                .mapToInt(room -> availabilityService.getMinAvailableRooms(room.getId(), checkinDate, checkoutDate))
                .max()
                .orElse(0); // Assume no double rooms if none match the filter
        hotelAvailabilityDTO.setMaxAvailableDoubleRooms(maxAvailableDoubleRooms);

        return hotelAvailabilityDTO;
    }

}