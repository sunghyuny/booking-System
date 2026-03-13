package com.booking.api.service;

import com.booking.api.dto.HotelDto;
import com.booking.api.entity.Hotel;
import com.booking.api.repository.HotelRepository;
import com.booking.api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<HotelDto.HotelResponse> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(HotelDto.HotelResponse::new)
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
