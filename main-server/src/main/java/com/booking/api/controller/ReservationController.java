package com.booking.api.controller;

import com.booking.api.dto.ReservationDto;
import com.booking.api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 객실 예약 성성 (인증된 사용자만 가능)
    @PostMapping
    public ResponseEntity<String> createReservation(@RequestBody ReservationDto.CreateRequest request) {
        Long reservationId = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("예약이 완료되었습니다. 예약 ID: " + reservationId);
    }
}
