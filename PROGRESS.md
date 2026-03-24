# 🏨 Booking System — 작업 기록

> 마지막 업데이트: 2026-03-17

---

## ✅ 완료된 작업

### 1. `.env` 환경변수 로드 문제 해결

**문제 원인 3가지:**

| # | 원인 | 해결 |
|---|------|------|
| 1 | `.env` 파일이 **UTF-16LE** 인코딩 | UTF-8(BOM 없음)으로 재저장 |
| 2 | Spring Boot가 `.env` 확장자 파싱 불가 | `application.yml`의 import를 `file:.env[.properties]`로 변경 |
| 3 | `TNS_ADMIN` 경로가 존재하지 않는 Downloads 폴더를 가리킴 | `oracle_wallet/` 경로로 수정 |

**추가 수정 — `build.gradle`:**
```groovy
bootRun {
    def envFile = file('.env')
    if (envFile.exists()) {
        envFile.readLines().each { line ->
            if (line.trim() && !line.trim().startsWith('#')) {
                def parts = line.split('=', 2)
                if (parts.length == 2) {
                    environment(parts[0].trim(), parts[1].trim())
                }
            }
        }
    }
}
```
→ Gradle이 `.env`를 직접 파싱해서 환경변수로 주입

**수정된 파일:**
- `main-server/.env` — UTF-8 재저장, 따옴표 제거, TNS_ADMIN 경로 수정
- `main-server/src/main/resources/application.yml` — datasource를 `${SPRING_DATASOURCE_*}`로 변경
- `main-server/build.gradle` — `bootRun` 블록 추가

---

### 2. 서버 기동 확인

- `.\gradlew.bat bootRun` 으로 **포트 8080** 정상 기동 확인
- Oracle Cloud DB (Wallet 연결) 정상 연결
- Redis (`localhost:6379`) 연결 설정 완료

**현재 열린 API 엔드포인트:**

| 메서드 | URL | 인증 |
|--------|-----|------|
| `POST` | `/api/v1/auth/signup` | 불필요 |
| `POST` | `/api/v1/auth/login` | 불필요 |
| `GET` | `/api/v1/hotels` | 불필요 |
| `GET` | `/api/v1/hotels/{id}/rooms` | 불필요 |
| `POST` | `/api/v1/reservations` | JWT 필요 |

---

## 📋 다음 작업 계획

### 백엔드 개선

- [ ] `RedisConfig.java` — `@EnableCaching` + `RedisCacheManager` (TTL 10분)
- [ ] `HotelService.getAllHotels()` — `@Cacheable(value = "hotels")` 추가
- [ ] `AuthController.login()` — JWT를 **HttpOnly 쿠키**로 반환
- [ ] `CorsConfig.java` — `localhost:5173` CORS 허용
- [ ] `JwtAuthenticationFilter` — 쿠키에서 JWT 추출하도록 수정

### 프론트엔드 구축 (`booking-System/frontend/`)

- [ ] React + Vite 프로젝트 초기화
- [ ] axios 인스턴스 (`withCredentials: true`)
- [ ] 페이지 5개 구현:
  - [ ] `/` — 호텔 목록
  - [ ] `/hotels/:id` — 호텔 상세 / 객실 목록
  - [ ] `/reserve/:roomId` — 예약
  - [ ] `/login` — 로그인
  - [ ] `/signup` — 회원가입
- [ ] Zustand 전역 상태 (로그인 여부)

---

## 🗂️ 프로젝트 구조

```
booking-System/
├── main-server/           # Spring Boot 백엔드 (포트 8080)
│   ├── src/main/java/com/booking/api/
│   │   ├── controller/    # AuthController, HotelController, ReservationController
│   │   ├── service/       # AuthService, HotelService, ReservationService
│   │   ├── security/      # SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter
│   │   ├── entity/        # User, Hotel, Room, Reservation
│   │   └── dto/           # AuthDto, HotelDto, ReservationDto
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── .env               # 환경변수 (DB URL, JWT 시크릿)
│   └── build.gradle
├── oracle_wallet/         # Oracle Cloud 지갑 (TNS 연결)
├── ai-server/             # AI 서버 (별도)
└── frontend/              # React + Vite (생성 예정)
```

---

## 🔧 서버 실행 방법

```powershell
# 백엔드
cd main-server
.\gradlew.bat bootRun

# 프론트엔드 (생성 후)
cd frontend
npm run dev
```

---

## ⚙️ 기술 스택

| 영역 | 기술 |
|------|------|
| 백엔드 | Spring Boot 4.0, Spring Security, JPA, JWT |
| DB | Oracle Cloud (Wallet 연결) |
| 캐시 | Redis (localhost:6379) |
| 메시지큐 | RabbitMQ (localhost:5672) |
| 프론트엔드 | React + Vite (예정) |
