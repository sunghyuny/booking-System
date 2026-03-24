package com.booking.api.service;

import com.booking.api.dto.HotelDto;
import com.booking.api.entity.Hotel;
import com.booking.api.entity.Room;
import com.booking.api.repository.HotelRepository;
import com.booking.api.repository.RoomRepository;
import com.booking.api.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "hotels", key = "{#checkIn, #checkOut}")
    public List<HotelDto.HotelResponse> getAllHotels(LocalDate checkIn, LocalDate checkOut) {
        return hotelRepository.findAll()
                .stream()
                .map(hotel -> {
                    List<Room> rooms = roomRepository.findByHotelId(hotel.getId());
                    int availableCount = 0;
                    
                    if (checkIn != null && checkOut != null) {
                        for (Room room : rooms) {
                            boolean isOverlapping = reservationRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThanAndStatusNot(
                                    room.getId(), checkOut, checkIn, "CANCELLED");
                            if (!isOverlapping) {
                                availableCount++;
                            }
                        }
                    } else {
                        availableCount = rooms.size();
                    }

                    int finalRoomCount = availableCount == 0 ? -1 : availableCount;
                    return new HotelDto.HotelResponse(hotel, finalRoomCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "hotelRooms", key = "#hotelId")
    public List<HotelDto.RoomResponse> getRoomsByHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid hotel ID: " + hotelId));

        return roomRepository.findByHotelId(hotel.getId())
                .stream()
                .map(HotelDto.RoomResponse::new)
                .collect(Collectors.toList());
    }
}
