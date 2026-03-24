package com.booking.api.scheduler;

import com.booking.api.service.HotelSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotelSyncScheduler {

    private final HotelSyncService hotelSyncService;

    // 매일 새벽 4시에 동기화 실행
    @Scheduled(cron = "0 0 4 * * ?")
    public void syncHotelsDaily() {
        log.info("Running daily hotel sync...");
        hotelSyncService.syncHotelsFromBookingCom();
    }

    // 개발 편의를 위해 애플리케이션 시작 시 바로 한 번 실행 (DB 초기화 목적)
    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        log.info("Running startup hotel sync...");
        hotelSyncService.syncHotelsFromBookingCom();
    }
}
