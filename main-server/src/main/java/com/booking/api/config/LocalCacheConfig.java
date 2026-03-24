package com.booking.api.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class LocalCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Redis 환경이 없으므로 로컬 서버 메모리(ConcurrentHashMap)를 활용하는 캐시 구현체 사용
        // "hotels", "hotelRooms" 2개의 캐시 공간을 미리 할당
        return new ConcurrentMapCacheManager("hotels", "hotelRooms");
    }
}
