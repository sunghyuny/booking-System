package com.booking.api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_seq")
    @SequenceGenerator(name = "reservation_seq", sequenceName = "RESERVATION_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(nullable = false, length = 20)
    private String status; // e.g., PENDING, CONFIRMED, CANCELLED

    // AI 예측 노쇼 확률을 관리자 대시보드에서 활용하기 위한 필드
    @Column(name = "ai_cancel_prob")
    private Double aiCancelProb;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Reservation(User user, Room room, LocalDate checkInDate, LocalDate checkOutDate, Integer totalPrice,
            String status) {
        this.user = user;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // AI 확률 업데이트를 위한 메서드
    public void updateAiCancelProb(Double aiCancelProb) {
        this.aiCancelProb = aiCancelProb;
    }

    // 예약 상태 변경을 위한 메서드
    public void updateStatus(String status) {
        this.status = status;
    }
}
