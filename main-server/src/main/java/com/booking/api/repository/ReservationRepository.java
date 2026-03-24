package com.booking.api.repository;

import com.booking.api.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // 객실의 예약 날짜가 겹치는지 확인하는 쿼리 (취소된 예약 제외)
    boolean existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThanAndStatusNot(Long roomId, LocalDate checkOutDate, LocalDate checkInDate, String status);

    // 객실의 예약된 날짜 목록 조회 (취소된 예약 제외)
    List<Reservation> findByRoomIdAndStatusNot(Long roomId, String status);

    // 사용자의 예약 내역을 최신순으로 조회
    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);
}
