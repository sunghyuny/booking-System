package com.booking.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SignupRequest {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "잘못된 이메일 형식입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String password;

        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        @NotBlank(message = "역할은 필수입니다.")
        private String role; // "USER", "HOTEL_ADMIN", "ADMIN"
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TokenResponse {
        private String accessToken;

        public TokenResponse(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
