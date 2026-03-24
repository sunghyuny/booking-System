package com.booking.api.service;

import com.booking.api.entity.Hotel;
import com.booking.api.entity.Room;
import com.booking.api.repository.HotelRepository;
import com.booking.api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelSyncService {

    private final RestTemplate restTemplate;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String apiHost;

    // 서울 지역 검색 API 예시 (실제 연동을 위해 더미 형태로 진행 후 변경 가능)
    @Transactional
    public void syncHotelsFromBookingCom() {
        log.info("Starting Booking.com Hotel Sync...");

        try {
            // RapidAPI 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", apiKey);
            headers.set("X-RapidAPI-Host", apiHost);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 1. 서울(Seoul)의 dest_id는 '-716583' (Booking.com 기준 서울 City ID)
            // 2. /api/v1/hotels/searchHotels 호출
            // [참고] Booking.com API v15 파라미터 (최근 구조 기준)
            String url = "https://" + apiHost + "/api/v1/hotels/searchHotels?dest_id=-716583&search_type=CITY&arrival_date=2026-04-01&departure_date=2026-04-02&adults=2&room_qty=1&page_number=1&units=metric&temperature_unit=c&languagecode=ko";

            log.info("Calling RapidAPI: {}", url);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                if (data.containsKey("hotels")) {
                    List<Map<String, Object>> hotels = (List<Map<String, Object>>) data.get("hotels");

                    log.info("Found {} hotels from API.", hotels.size());

                    int count = 0;
                    for (Map<String, Object> h : hotels) {
                        if (count >= 10) break; // 너무 많으면 느려지니 최대 10개만 저장

                        // 호텔 속성 추출 (API 응답 구조에 맞게 방어적 파싱)
                        Map<String, Object> property = (Map<String, Object>) h.get("property");
                        if (property == null) continue;

                        String name = (String) property.get("name");
                        String location = "Seoul";
                        List<String> latitudeLongitude = (List<String>) property.get("latitudeLongitude");
                        if (latitudeLongitude != null && latitudeLongitude.size() >= 2) {
                            location = "Seoul (Lat: " + latitudeLongitude.get(0) + ", Lng: " + latitudeLongitude.get(1) + ")";
                        }
                        
                        Double reviewScore = property.get("reviewScore") != null ? Double.valueOf(property.get("reviewScore").toString()) : 0.0;
                        String description = "평점: " + reviewScore + " / Booking.com 연동 숙소";

                        // 이미 존재하는지 이름으로 확인
                        if (hotelRepository.findAll().stream().noneMatch(existing -> existing.getName().equals(name))) {
                            Hotel newHotel = Hotel.builder()
                                    .name(name)
                                    .address(location)
                                    .description(description)
                                    .build();
                            
                            Hotel savedHotel = hotelRepository.save(newHotel);
                            
                            // 기본 객실 2개씩 생성 
                            roomRepository.save(Room.builder()
                                    .hotel(savedHotel)
                                    .roomNumber("101")
                                    .roomType("스탠다드 더블룸")
                                    .price(150000 + (int)(Math.random() * 50000)) // 15~20만 랜던
                                    .capacity(2)
                                    .build());
                                    
                            roomRepository.save(Room.builder()
                                    .hotel(savedHotel)
                                    .roomNumber("201")
                                    .roomType("디럭스 트윈룸")
                                    .price(250000 + (int)(Math.random() * 50000)) // 25~30만 랜덤
                                    .capacity(3)
                                    .build());
                            
                            count++;
                        }
                    }
                }
            }
            log.info("Successfully completed RapidAPI hotel sync.");
        } catch (Exception e) {
            log.error("RapidAPI sync failed: {}", e.getMessage(), e);
        }
    }
}
