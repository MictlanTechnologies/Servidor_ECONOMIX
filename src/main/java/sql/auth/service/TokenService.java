package sql.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sql.auth.model.RefreshToken;
import sql.auth.repository.RefreshTokenRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final byte[] jwtSecret;
    private final SecureRandom secureRandom = new SecureRandom();

    public TokenService(RefreshTokenRepository refreshTokenRepository,
                        @Value("${economix.jwt.secret:}") String configuredSecret) {
        this.refreshTokenRepository = refreshTokenRepository;
        if (configuredSecret == null || configuredSecret.isBlank()) {
            throw new IllegalStateException("Missing required property economix.jwt.secret (env: ECONOMIX_JWT_SECRET)");
        }
        byte[] rawUtf8 = configuredSecret.getBytes(StandardCharsets.UTF_8);
        if (rawUtf8.length >= 32) {
            this.jwtSecret = rawUtf8;
            return;
        }

        byte[] decodedBase64;
        try {
            decodedBase64 = Base64.getDecoder().decode(configuredSecret);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("economix.jwt.secret must have at least 32 bytes");
        }

        if (decodedBase64.length < 32) {
            throw new IllegalStateException("economix.jwt.secret must have at least 32 bytes");
        }
        this.jwtSecret = decodedBase64;
    }

    public String generateAccessToken(Integer userId, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + 15L * 60L * 1000L);
        return Jwts.builder()
                .claims(Map.of("userId", userId, "roles", roles))
                .issuedAt(now)
                .expiration(exp)
                .signWith(Keys.hmacShaKeyFor(jwtSecret))
                .compact();
    }

    public Claims parseAccessToken(String jwt) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret))
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public String issueRefreshToken(Integer userId, String deviceInfo) {
        String raw = randomToken();
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .tokenHash(sha256(raw))
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .deviceInfo(deviceInfo)
                .build();
        refreshTokenRepository.save(token);
        return raw;
    }

    public RefreshToken validateRefreshToken(String refreshToken) {
        String hash = sha256(refreshToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash).orElse(null);
        if (token == null || token.getRevokedAt() != null || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }
        return token;
    }

    public String rotateRefreshToken(String refreshToken) {
        RefreshToken current = validateRefreshToken(refreshToken);
        if (current == null) {
            return null;
        }
        current.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(current);
        return issueRefreshToken(current.getUserId(), current.getDeviceInfo());
    }

    public void revokeRefreshToken(String refreshToken) {
        String hash = sha256(refreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
        });
    }

    private String randomToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash refresh token", ex);
        }
    }
}
