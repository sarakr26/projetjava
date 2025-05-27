package edu.sabanciuniv.hotelbookingapp.service.impl;

import edu.sabanciuniv.hotelbookingapp.exception.HotelAlreadyExistsException;
import edu.sabanciuniv.hotelbookingapp.model.*;
import edu.sabanciuniv.hotelbookingapp.model.dto.*;
import edu.sabanciuniv.hotelbookingapp.repository.HotelAmenityRepository;
import edu.sabanciuniv.hotelbookingapp.repository.HotelRepository;
import edu.sabanciuniv.hotelbookingapp.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AddressService addressService;
    private final RoomService roomService;
    private final UserService userService;
    private final HotelManagerService hotelManagerService;
    private final HotelAmenityRepository hotelAmenityRepository;

    @Override
    @Transactional
    public Hotel saveHotel(HotelRegistrationDTO hotelRegistrationDTO) {
        log.info("Attempting to save a new hotel: {}", hotelRegistrationDTO.toString());

        Optional<Hotel> existingHotel = hotelRepository.findByName(hotelRegistrationDTO.getName());
        if (existingHotel.isPresent()) {
            throw new HotelAlreadyExistsException("This hotel name is already registered!");
        }

        Hotel hotel = mapHotelRegistrationDtoToHotel(hotelRegistrationDTO);
        Address savedAddress = addressService.saveAddress(hotelRegistrationDTO.getAddressDTO());
        hotel.setAddress(savedAddress);

        // Get the username of the currently logged-in hotel manager
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Retrieve the Hotel Manager associated with this username
        HotelManager hotelManager = hotelManagerService.findByUser(userService.findUserByUsername(username));
        hotel.setHotelManager(hotelManager);

        // Saving hotel to be able to bind rooms to hotel id
        final Hotel savedHotel = hotelRepository.save(hotel);

        List<Room> savedRooms = roomService.saveRooms(hotelRegistrationDTO.getRoomDTOs(), savedHotel);
        savedHotel.setRooms(savedRooms);

        // Save hotel amenities
        if (hotelRegistrationDTO.getAmenities() != null) {
            List<HotelAmenity> amenities = hotelRegistrationDTO.getAmenities().stream()
                .filter(HotelAmenityDTO::isAvailable)
                .map(amenityDTO -> HotelAmenity.builder()
                    .hotel(savedHotel)  // Using savedHotel instead of hotel
                    .serviceType(amenityDTO.getServiceType())
                    .pricePerDay(amenityDTO.getPricePerDay())
                    .available(true)
                    .build())
                .collect(Collectors.toList());
            
            hotelAmenityRepository.saveAll(amenities);
            savedHotel.setAmenities(amenities);
        }

        return hotelRepository.save(savedHotel);
    }

    @Override
    public HotelDTO findHotelDtoByName(String name) {
        Hotel hotel = hotelRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        return mapHotelToHotelDto(hotel);
    }

    @Override
    public HotelDTO findHotelDtoById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        return mapHotelToHotelDto(hotel);
    }

    @Override
    public Optional<Hotel> findHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    @Override
    public List<HotelDTO> findAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(this::mapHotelToHotelDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelDTO updateHotel(HotelDTO hotelDTO) {
        log.info("Attempting to update hotel with ID: {}", hotelDTO.getId());

        Hotel existingHotel = hotelRepository.findById(hotelDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));

        if (hotelNameExistsAndNotSameHotel(hotelDTO.getName(), hotelDTO.getId())) {
            throw new HotelAlreadyExistsException("This hotel name is already registered!");
        }

        existingHotel.setName(hotelDTO.getName());

        Address updatedAddress = addressService.updateAddress(hotelDTO.getAddressDTO());
        existingHotel.setAddress(updatedAddress);

        hotelDTO.getRoomDTOs().forEach(roomService::updateRoom);

        hotelRepository.save(existingHotel);
        log.info("Successfully updated existing hotel with ID: {}", hotelDTO.getId());
        return mapHotelToHotelDto(existingHotel);
    }

    @Override
    public void deleteHotelById(Long id) {
        log.info("Attempting to delete hotel with ID: {}", id);
        hotelRepository.deleteById(id);
        log.info("Successfully deleted hotel with ID: {}", id);
    }
    @Override
    public List<Hotel> findAllHotelsByManagerId(Long managerId) {
        List<Hotel> hotels = hotelRepository.findAllByHotelManager_Id(managerId);
        return (hotels != null) ? hotels : Collections.emptyList();
    }

    @Override
    public List<HotelDTO> findAllHotelDtosByManagerId(Long managerId) {
        List<Hotel> hotels = hotelRepository.findAllByHotelManager_Id(managerId);
        if (hotels != null) {
            return hotels.stream()
                    .map(this::mapHotelToHotelDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public HotelDTO findHotelByIdAndManagerId(Long hotelId, Long managerId) {
        Hotel hotel = hotelRepository.findByIdAndHotelManager_Id(hotelId, managerId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        return mapHotelToHotelDto(hotel);
    }

    @Override
    @Transactional
    public HotelDTO updateHotelByManagerId(HotelDTO hotelDTO, Long managerId) {
        log.info("Attempting to update hotel with ID: {} for Manager ID: {}", hotelDTO.getId(), managerId);

        Hotel existingHotel = hotelRepository.findByIdAndHotelManager_Id(hotelDTO.getId(), managerId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));

        if (hotelNameExistsAndNotSameHotel(hotelDTO.getName(), hotelDTO.getId())) {
            throw new HotelAlreadyExistsException("This hotel name is already registered!");
        }

        existingHotel.setName(hotelDTO.getName());

        Address updatedAddress = addressService.updateAddress(hotelDTO.getAddressDTO());
        existingHotel.setAddress(updatedAddress);

        hotelDTO.getRoomDTOs().forEach(roomService::updateRoom);

        // Update amenities
        if (hotelDTO.getAmenities() != null) {
            // Create a map of submitted amenities for easier lookup
            java.util.Map<Long, HotelAmenityDTO> submittedAmenitiesMap = hotelDTO.getAmenities().stream()
                    .collect(java.util.stream.Collectors.toMap(HotelAmenityDTO::getId, amenityDTO -> amenityDTO));

            // Iterate through existing amenities and update or remove based on submitted data
            existingHotel.getAmenities().removeIf(existingAmenity -> {
                HotelAmenityDTO submittedAmenityDTO = submittedAmenitiesMap.get(existingAmenity.getId());
                if (submittedAmenityDTO != null) {
                    // Update existing amenity properties
                    existingAmenity.setAvailable(submittedAmenityDTO.isAvailable());
                    existingAmenity.setPricePerDay(submittedAmenityDTO.getPricePerDay());
                    return false; // Keep this amenity
                } else {
                    // This existing amenity was not in the submitted list, remove it
                    return true; // Remove this amenity
                }
            });
        }

        hotelRepository.save(existingHotel);
        log.info("Successfully updated existing hotel with ID: {} for Manager ID: {}", hotelDTO.getId(), managerId);
        return mapHotelToHotelDto(existingHotel);
    }

    @Override
    public void deleteHotelByIdAndManagerId(Long hotelId, Long managerId) {
        log.info("Attempting to delete hotel with ID: {} for Manager ID: {}", hotelId, managerId);
        Hotel hotel = hotelRepository.findByIdAndHotelManager_Id(hotelId, managerId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        hotelRepository.delete(hotel);
        log.info("Successfully deleted hotel with ID: {} for Manager ID: {}", hotelId, managerId);
    }

    private Hotel mapHotelRegistrationDtoToHotel(HotelRegistrationDTO dto) {
        return Hotel.builder()
                .name(formatText(dto.getName()))
                .build();
    }

    @Override
    public HotelDTO mapHotelToHotelDto(Hotel hotel) {
        List<RoomDTO> roomDTOs = hotel.getRooms().stream()
                .map(roomService::mapRoomToRoomDto)
                .collect(Collectors.toList());

        AddressDTO addressDTO = addressService.mapAddressToAddressDto(hotel.getAddress());

        List<HotelAmenityDTO> amenityDTOs = hotel.getAmenities().stream()
                .map(amenity -> HotelAmenityDTO.builder()
                        .id(amenity.getId())
                        .serviceType(amenity.getServiceType())
                        .pricePerDay(amenity.getPricePerDay())
                        .available(amenity.isAvailable())
                        .build())
                .collect(Collectors.toList());

        return HotelDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .addressDTO(addressDTO)
                .roomDTOs(roomDTOs)
                .amenities(amenityDTOs)
                .managerUsername(hotel.getHotelManager().getUser().getUsername())
                .build();
    }

    private boolean hotelNameExistsAndNotSameHotel(String name, Long hotelId) {
        Optional<Hotel> existingHotelWithSameName = hotelRepository.findByName(name);
        return existingHotelWithSameName.isPresent() && !existingHotelWithSameName.get().getId().equals(hotelId);
    }

    private String formatText(String text) {
        return StringUtils.capitalize(text.trim());
    }

}

