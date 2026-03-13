package com.booking.api.dto;

import com.booking.api.entity.Hotel;
import com.booking.api.entity.Room;
import lombok.Getter;
import lombok.Setter;

public class HotelDto {

    @Getter
    @Setter
    public static class HotelResponse {
        private Long id;
        private String name;
        private String address;
        private String description;

        public HotelResponse(Hotel hotel) {
            this.id = hotel.getId();
            this.name = hotel.getName();
            this.address = hotel.getAddress();
            this.description = hotel.getDescription();
        }
    }

    @Getter
    @Setter
    public static class RoomResponse {
        private Long id;
        private Long hotelId;
        private String roomNumber;
        private String roomType;
        private Integer price;
        private Integer capacity;

        public RoomResponse(Room room) {
            this.id = room.getId();
            this.hotelId = room.getHotel().getId();
            this.roomNumber = room.getRoomNumber();
            this.roomType = room.getRoomType();
            this.price = room.getPrice();
            this.capacity = room.getCapacity();
        }
    }
}
