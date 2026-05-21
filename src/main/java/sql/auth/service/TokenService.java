package sql.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sql.auth.model.RefreshToken;
import sql.auth.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public String generateAccessToken(Integer userId) {
        return "atk_" + userId + "_" + UUID.randomUUID();
    }

    public String issueRefreshToken(Integer userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .idUsuario(userId)
                .token("rtk_" + UUID.randomUUID())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken).getToken();
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken rt = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido."));
        if (Boolean.TRUE.equals(rt.getRevoked()) || rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado o revocado.");
        }
        return rt;
    }

    public void revokeByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }
}
