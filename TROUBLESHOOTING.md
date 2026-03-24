# 프로젝트 에러 및 해결 내역 (Troubleshooting History)

지금까지 Booking System 프로젝트를 설계 및 구현하면서 마주쳤던 주요 에러(이슈)들과 그 해결 과정을 정리한 문서입니다.

## 1. Oracle DB Wallet 경로(공백) 인식 에러
- **문제 상황**: Windows 환경에서 Oracle DB Wallet 경로에 공백이 포함된 폴더명이 있을 경우, 스프링 부트가 경로를 제대로 인식할 수 없어 DB 연결에 실패하는 문제.
- **해결 방안**: 바탕화면에 공백이 없는 별도의 폴더(`c:\Users\zerax\Desktop\...`)를 만들어 Wallet 파일들을 이동시킨 후, `application.yml` 및 `.env`에서 새 경로를 참조하도록 DB 접속 정보를 고도화함.

## 2. 동시 예약 시 오버부킹(Race Condition) 에러 방지
- **문제 상황**: 다수의 사용자가 동시에 동일한 객실을 예약 시도 시, 남은 객실 수나 상태 체크 로직이 겹치면서 이미 예약된 방이 중복 예약 결제되는 데이터 정합성 파괴 위험.
- **해결 방안**: JPA DB 락 기술인 **비관적 락(Pessimistic Lock)** 을 도입시킴. `RoomRepository.findByIdWithPessimisticLock` 메서드를 구현하여 한 트랜잭션이 객실 데이터를 읽어 예약을 완료할 때까지 다른 트랜잭션이 읽거나 쓸 수 없도록 직렬화(Serialize) 처리함.

## 3. 프론트엔드 - 백엔드 통신 간 CORS (Cross-Origin Resource Sharing) 에러
- **문제 상황**: React 프론트엔드(`localhost:5173`)에서 Spring Boot 백엔드 API 서버(`localhost:8080`) 호출 시 브라우저 동일 출처 정책(SOP) 보안으로 인해 통신이 차단됨.
- **해결 방안**: 
  - 커스텀 `CorsConfig.java`를 생성하여 프론트엔드의 도메인(`localhost:5173`)에 대한 접근을 전역적으로 허용(`AllowedOrigins`).
  - 추가로 JWT(쿠키) 전달을 원활히 하기 위해 클라이언트의 `axios` 설정에 `withCredentials: true`를 추가하고, 백엔드에는 `AllowCredentials(true)` 속성을 동기화시킴.

## 4. 무거워진 세션 및 JWT 탈취 보안 취약점
- **문제 상황**: 기존 세션 방식은 MSA 환경 및 다중 서버 접속(AI 서버 등)에서 스케일아웃 및 관리가 어려우며, JWT를 로컬 스토리지에 단순 보관하면 XSS 공격에 의한 토큰 탈취 우려가 있음.
- **해결 방안**:
  1. 서버 세션을 무상태(Stateless)로 변경하고 완전한 **JWT(JSON Web Token) 기반 인증**을 도입(`JwtTokenProvider` & `JwtAuthenticationFilter`).
  2. 프론트엔드로 Access Token 반환 시, 자바스크립트에서 접근할 수 없도록 **HttpOnly 쿠키**에 담아서 전송하여 XSS 기반의 강제 탈취를 원천 차단함.

## 5. 빈번한 호텔/객실 조회 시 DB 부하 (Latency) 문제
- **문제 상황**: 메인 페이지의 호텔 리스트나 상세 페이지 등 동일한 데이터를 빈번하게 리로드할 경우 Oracle DB에 엄청난 양의 무거운 Select 쿼리가 집중되어 전체 API 처리 지연 발생.
- **해결 방안**: Spring Data Redis를 연동하고 `HotelService` 구현체에 `@Cacheable(value = "hotels")` 등의 어노테이션 적용. 이를 통해 한 번 조회된 결과는 10분(TTL) 동안 빠르고 가벼운 메모리(Redis)단에서 즉각 응답하게 하여 DB 트래픽을 극적으로 감소시킴.

## 6. 객실 예약 시 400 Bad Request (The given id must not be null) 및 White-out 크래시
- **문제 상황**: 프론트엔드에서 예약 확정(Confirm Booking) 버튼 클릭 시 백엔드가 400 응답을 내리고, 이를 처리하지 못한 프론트 화면이 백지화되는 버그 발생.
- **해결 방안**: 
  1. (백엔드) `ReservationService`에서 프론트가 보내지 않는 `hotelId`를 불필요하게 검증하다 실패하지 않도록 조치. 방 정보(`room`)에서 직접 호텔을 참조(`room.getHotel()`)하여 오작동 제거.
  2. (프론트엔드) 백엔드 DTO에 맞도록 키를 `checkInDate`, `checkOutDate`로 정상 일치시키고, 에러 응답이 JSON 객체로 넘어올 때 `message` 필드를 파싱하여 안전하게 렌더링되게 만듦.
