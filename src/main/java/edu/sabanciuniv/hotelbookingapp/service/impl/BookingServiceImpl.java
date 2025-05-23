package edu.sabanciuniv.hotelbookingapp.service.impl;

import edu.sabanciuniv.hotelbookingapp.model.*;
import edu.sabanciuniv.hotelbookingapp.model.dto.AddressDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.BookingDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.BookingInitiationDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.RoomSelectionDTO;
import edu.sabanciuniv.hotelbookingapp.repository.BookingRepository;
import edu.sabanciuniv.hotelbookingapp.service.*;
import edu.sabanciuniv.hotelbookingapp.chain.*;
import edu.sabanciuniv.hotelbookingapp.strategy.*;
import edu.sabanciuniv.hotelbookingapp.observer.*;
import edu.sabanciuniv.hotelbookingapp.builder.BookingBuilder;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final HotelService hotelService;

    @Override
    @Transactional
    public BookingDTO confirmBooking(BookingInitiationDTO bookingInitiationDTO, Long customerId) {
        try {
            log.info("Starting booking confirmation process for customer: {}", customerId);
            
            // Save initial booking
            Booking booking = saveBooking(bookingInitiationDTO, customerId);
            booking = bookingRepository.saveAndFlush(booking);
            
            // Process payment
            Payment payment = paymentService.savePayment(bookingInitiationDTO, booking);
            
            // Update booking with payment reference
            booking.setPayment(payment);
            booking = bookingRepository.saveAndFlush(booking);
            
            log.info("Booking confirmed successfully. ID: {}, Payment ID: {}", 
                    booking.getId(), payment.getId());
            
            return mapBookingModelToBookingDto(booking);
        } catch (Exception e) {
            log.error("Error confirming booking: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to confirm booking", e);
        }
    }

    @Override
    @Transactional
    public Booking saveBooking(BookingInitiationDTO bookingInitiationDTO, Long customerId) {
        Customer customer = customerService.findByUserId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Hotel hotel = hotelService.findHotelById(bookingInitiationDTO.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));

        Booking booking = Booking.builder()
                .customer(customer)
                .hotel(hotel)
                .checkinDate(bookingInitiationDTO.getCheckinDate())
                .checkoutDate(bookingInitiationDTO.getCheckoutDate())
                .bookedRooms(new ArrayList<>())
                .build();

        // Add booked rooms
        for (RoomSelectionDTO roomSelection : bookingInitiationDTO.getRoomSelections()) {
            if (roomSelection.getCount() > 0) {
                BookedRoom bookedRoom = BookedRoom.builder()
                        .booking(booking)
                        .roomType(roomSelection.getRoomType())
                        .count(roomSelection.getCount())
                        .build();
                booking.getBookedRooms().add(bookedRoom);
            }
        }

        return bookingRepository.saveAndFlush(booking);
    }

    @Override
    public List<BookingDTO> findAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::mapBookingModelToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO findBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));
        return mapBookingModelToBookingDto(booking);
    }

    @Override
    public List<BookingDTO> findBookingsByCustomerId(Long customerId) {
        List<Booking> bookingDTOs = bookingRepository.findBookingsByCustomerId(customerId);
        return bookingDTOs.stream()
                .map(this::mapBookingModelToBookingDto)
                .sorted(Comparator.comparing(BookingDTO::getCheckinDate))
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO findBookingByIdAndCustomerId(Long bookingId, Long customerId) {
        Booking booking = bookingRepository.findBookingByIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));
        return mapBookingModelToBookingDto(booking);
    }

    @Override
    public List<BookingDTO> findBookingsByManagerId(Long managerId) {
        List<Hotel> hotels = hotelService.findAllHotelsByManagerId(managerId);
        return hotels.stream()
                .flatMap(hotel -> bookingRepository.findBookingsByHotelId(hotel.getId()).stream())
                .map(this::mapBookingModelToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO findBookingByIdAndManagerId(Long bookingId, Long managerId) {
        Booking booking = bookingRepository.findBookingByIdAndHotel_HotelManagerId(bookingId, managerId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId + " and manager ID: " + managerId));
        return mapBookingModelToBookingDto(booking);
    }

    private void validateBookingDates(LocalDate checkinDate, LocalDate checkoutDate) {
        if (checkinDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        if (checkoutDate.isBefore(checkinDate.plusDays(1))) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }

    @Override
    public BookingDTO mapBookingModelToBookingDto(Booking booking) {
        AddressDTO addressDto = AddressDTO.builder()
                .addressLine(booking.getHotel().getAddress().getAddressLine())
                .city(booking.getHotel().getAddress().getCity())
                .country(booking.getHotel().getAddress().getCountry())
                .build();

        List<RoomSelectionDTO> roomSelections = booking.getBookedRooms().stream()
                .map(room -> RoomSelectionDTO.builder()
                        .roomType(room.getRoomType())
                        .count(room.getCount())
                        .build())
                .collect(Collectors.toList());

        User customerUser = booking.getCustomer().getUser();

        return BookingDTO.builder()
                .id(booking.getId())
                .confirmationNumber(booking.getConfirmationNumber())
                .bookingDate(booking.getBookingDate())
                .customerId(booking.getCustomer().getId())
                .hotelId(booking.getHotel().getId())
                .checkinDate(booking.getCheckinDate())
                .checkoutDate(booking.getCheckoutDate())
                .roomSelections(roomSelections)
                .totalPrice(booking.getPayment().getTotalPrice())
                .hotelName(booking.getHotel().getName())
                .hotelAddress(addressDto)
                .customerName(customerUser.getName() + " " + customerUser.getLastName())
                .customerEmail(customerUser.getUsername())
                .paymentStatus(booking.getPayment().getPaymentStatus())
                .paymentMethod(booking.getPayment().getPaymentMethod())
                .build();
    }

}
