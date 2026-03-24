package com.booking.api.controller;

import com.booking.api.dto.ApiResponse;
import com.booking.api.dto.AuthDto;
import com.booking.api.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody AuthDto.SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "회원가입이 완료되었습니다.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody AuthDto.LoginRequest request,
            HttpServletResponse response) {

        AuthDto.TokenResponse tokenResponse = authService.login(request);

        // JWT를 HttpOnly 쿠키로 설정
        Cookie jwtCookie = new Cookie("jwt", tokenResponse.getAccessToken());
        jwtCookie.setHttpOnly(true);    // JS에서 접근 불가 (XSS 방어)
        jwtCookie.setSecure(false);     // 개발환경: false, 프로덕션: true (HTTPS)
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60);   // 1시간 (초 단위)
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(new ApiResponse<>(true, "로그인이 완료되었습니다.", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        // 쿠키 만료 처리
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        return ResponseEntity.ok(new ApiResponse<>(true, "로그아웃이 완료되었습니다.", null));
    }
}
