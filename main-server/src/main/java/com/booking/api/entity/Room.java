package com.booking.api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_seq")
    @SequenceGenerator(name = "room_seq", sequenceName = "ROOM_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Column(name = "room_type", nullable = false, length = 50)
    private String roomType; // e.g., SINGLE, DOUBLE, SUITE

    @Column(nullable = false)
    private Integer price; // 1박 요금

    @Column(nullable = false)
    private Integer capacity; // 최대 수용 인원

    @Builder
    public Room(Hotel hotel, String roomNumber, String roomType, Integer price, Integer capacity) {
        this.hotel = hotel;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.capacity = capacity;
    }
}
