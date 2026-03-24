package com.booking.api.service;

import com.booking.api.dto.ReservationDto;
import com.booking.api.entity.Reservation;
import com.booking.api.entity.Room;
import com.booking.api.entity.User;
import com.booking.api.repository.ReservationRepository;
import com.booking.api.repository.RoomRepository;
import com.booking.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createReservation(ReservationDto.CreateRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        // 트랜잭션 내에서 비관적 락(Pessimistic-Write)이 걸린 상태로 Room 조회
        // 이로써 다른 트랜잭션이 해당 객실을 동시에 예약(수정)하는 것을 DB 레벨에서 차단함
        Room room = roomRepository.findByIdWithPessimisticLock(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 숙박 일수 계산 및 총 요금 산정
        long daysBetween = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (daysBetween <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // 중복 예약 겹침 검증 로직 (취소된 예약건은 무시)
        boolean isOverlapping = reservationRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThanAndStatusNot(
                room.getId(), request.getCheckOutDate(), request.getCheckInDate(), "CANCELLED");
        if (isOverlapping) {
            throw new IllegalArgumentException("선택하신 날짜에 이미 객실 예약이 차 있습니다.");
        }

        int basePrice = (int) (room.getPrice() * daysBetween);
        int extraFee = 0;
        if (request.getGuestCount() != null && request.getGuestCount() > room.getCapacity()) {
            int extraGuestCount = request.getGuestCount() - room.getCapacity();
            extraFee = extraGuestCount * 20000 * (int) daysBetween;
        }
        int totalPrice = basePrice + extraFee;

        Reservation reservation = Reservation.builder()
                .user(user)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .status("CONFIRMED")
                .guestName(request.getGuestName())
                .guestPhone(request.getGuestPhone())
                .guestCount(request.getGuestCount())
                .specialRequests(request.getSpecialRequests())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // TO-DO: RabbitMQ에 예약 이벤트 발행 (AI 예측 서버 전달용)
        log.info("Reservation created successfully. ID: {}, Room: {}", savedReservation.getId(), room.getRoomNumber());

        return savedReservation.getId();
    }

    @Transactional(readOnly = true)
    public List<ReservationDto.ReservationResponse> getMyReservations() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Reservation> reservations = reservationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return reservations.stream()
                .map(r -> new ReservationDto.ReservationResponse(
                        r.getId(),
                        r.getRoom().getHotel().getName(),
                        r.getRoom().getRoomNumber(),
                        r.getCheckInDate(),
                        r.getCheckOutDate(),
                        r.getTotalPrice(),
                        r.getStatus(),
                        r.getAiCancelProb()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto.BookedDateResponse> getBookedDates(Long roomId) {
        return reservationRepository.findByRoomIdAndStatusNot(roomId, "CANCELLED")
                .stream()
                .map(r -> new ReservationDto.BookedDateResponse(r.getCheckInDate(), r.getCheckOutDate()))
                .collect(Collectors.toList());
    }
}
