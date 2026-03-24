package com.booking.api.controller;

import com.booking.api.dto.HotelDto;
import com.booking.api.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    // 전체 호텔 조회
    @GetMapping
    public ResponseEntity<List<HotelDto.HotelResponse>> getAllHotels(
            @RequestParam(required = false) LocalDate checkInDate,
            @RequestParam(required = false) LocalDate checkOutDate) {
        return ResponseEntity.ok(hotelService.getAllHotels(checkInDate, checkOutDate));
    }

    // 특정 호텔의 잔여 객실 조회
    @GetMapping("/{id}/rooms")
    public ResponseEntity<List<HotelDto.RoomResponse>> getRoomsByHotel(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getRoomsByHotel(id));
    }
}
