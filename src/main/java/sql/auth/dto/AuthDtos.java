package sql.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class AuthDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String usernameOrEmail;
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Integer userId;
        private String username;
        private List<String> roles;
        private boolean twoFactorEnabled;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private boolean requires2fa;
        private String challengeId;
        private LocalDateTime challengeExpiresAt;
        private String accessToken;
        private String refreshToken;
        private UserInfo userInfo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshRequest {
        private String refreshToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Verify2FARequest {
        private String challengeId;
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TwoFactorSetupResponse {
        private String otpauthUri;
        private String secretMasked;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TwoFactorToggleResponse {
        private boolean twoFactorEnabled;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TwoFactorOtpRequest {
        private String otpCode;
    }
}
