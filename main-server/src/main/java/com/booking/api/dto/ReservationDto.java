package com.booking.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class ReservationDto {

    @Getter
    @Setter
    public static class CreateRequest {
        private Long hotelId;
        private Long roomId;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
    }

    @Getter
    @Setter
    public static class ReservationResponse {
        private Long reservationId;
        private String hotelName;
        private String roomNumber;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private Integer totalPrice;
        private String status;
        private Double aiCancelProb;

        public ReservationResponse(Long reservationId, String hotelName, String roomNumber,
                LocalDate checkInDate, LocalDate checkOutDate,
                Integer totalPrice, String status, Double aiCancelProb) {
            this.reservationId = reservationId;
            this.hotelName = hotelName;
            this.roomNumber = roomNumber;
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
            this.totalPrice = totalPrice;
            this.status = status;
            this.aiCancelProb = aiCancelProb;
        }
    }
}
