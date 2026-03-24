# Booking System Workflow & Modification History

이 문서는 최근 예약 시스템의 버그 수정 및 기능 개선 내역을 요약하고 추적하기 위한 파일입니다.

## 1. DTO & Frontend Payload 불일치 수정
- **문제**: 예약 시 백엔드 단에서 `NullPointerException (The given id must not be null)` 발생.
- **수정사항**: 
  - `Reserve.jsx`에서 `checkIn`, `checkOut` 필드명을 백엔드의 `ReservationDto.CreateRequest`와 매핑되도록 `checkInDate`, `checkOutDate`로 통일했습니다.
  - `ReservationService.java`에서 불필요하게 `hotelId`를 조회하지 않고, 예약 객실(`room`)에서 직접 호텔을 참고(`room.getHotel()`)하도록 수정했습니다.

## 2. 프론트엔드 White-out 크래시 보호
- **문제**: 백엔드에서 에러 메시지가 JSON 형태의 Object로 반환될 경우, 프론트에서 이를 그대로 렌더링하려 하다가 리액트 트리가 부서지는 오류(White-out) 발생.
- **수정사항**: `err.response?.data?.message`를 우선적으로 추출하여 문자열(String)만 안전하게 에러 알림창에 렌더링되도록 방어 로직을 추가했습니다.

## 3. 예약 날짜 중복(Overlap) 원천 차단
- **문제**: 같은 객실에 대해 여러 사람이 동일한 날짜 혹은 겹치는 날짜로 중복 예약이 가능한 구조적 결함.
- **수정사항**: 
  - `ReservationRepository`에 `existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThanAndStatus` 구문을 추가하여 `CONFIRMED` 상태의 겹치는 예약이 있는지 사전에 조회합니다.
  - 겹침이 감지되면 예약 생성 전 400 에러를 던져 중복을 방지합니다.

## 4. '내 예약 내역' 페이지 (My Reservations) 신설
- **문제**: 유저가 실제로 결제하거나 예약한 객실 리스트를 확인할 방법이 존재하지 않음.
- **수정사항**: 
  - 백엔드에 `GET /api/v1/reservations/me` 컨트롤러 및 서비스 연동 구현 (`findByUserIdOrderByCreatedAtDesc`).
  - 프론트엔드 네비게이션(Navbar)에 '내 예약' 링크를 노출하고 `MyReservations.jsx` 페이지 신설.

## 5. 예약 전 상세 정보(Hotel & Capacity) 보강
- **문제**: 결제/예약 확정 버튼을 누르기 전, 1박 요금만 표시되어 있어 호텔 이름이나 수용 인원을 확인하기 불충분.
- **수정사항**: `HotelDto`의 `RoomResponse`에 `hotelName`을 추가로 매핑하여 반환하고, 프론트의 UI에 카드 패널을 도입해 직관적인 UI(호텔 이름, 최대 수용 인원 정보)를 확충.

## 6. 상세 예약 폼 업데이트 및 동적 인원 초과금 반영
- **문제**: 실제 서비스 관점에서 성명, 연락처 등의 부가 예약 정보를 받지 않고 있었으며, 좁은 다크 테마 레이아웃으로 인해 직관성이 저하됨.
- **수정사항**:
  - `Reservation`, `ReservationDto` 스키마에 `guestName`, `guestPhone`, `guestCount`, `specialRequests` 4개의 메타데이터 필드를 확장. (체크인 예정 시간은 호텔 통제 항목이므로 예약 양식에서 제거.)
  - `ReservationService` 비즈니스 로직에서 기준 객실 수용 인원(`capacity`) 대비 초과된 인원에 대해 **초과금(1인당 1박 +20,000원)**을 동적으로 부과하도록 가격 계산 로직 재정립.
  - PC 모니터 화면에 최적화된 Bright Theme(라이트 테마 / Tailwind Blue 포인트)로 디자인을 대대적으로 리모델링하고, 예약 페이지(`Reserve.jsx`)를 좌우 가로-분할(Side-by-side) 방식으로 개편.
