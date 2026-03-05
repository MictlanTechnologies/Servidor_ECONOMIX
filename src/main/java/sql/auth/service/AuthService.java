package sql.auth.service;

import org.springframework.stereotype.Service;
import sql.auth.dto.AuthDtos;
import sql.auth.model.AuthChallenge;
import sql.auth.model.RefreshToken;
import sql.auth.repository.AuthChallengeRepository;
import sql.auth.util.SimpleRateLimiter;
import sql.model.Usuario;
import sql.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthChallengeRepository authChallengeRepository;
    private final TokenService tokenService;
    private final TwoFactorService twoFactorService;
    private final SimpleRateLimiter rateLimiter;

    public AuthService(UsuarioRepository usuarioRepository,
                       AuthChallengeRepository authChallengeRepository,
                       TokenService tokenService,
                       TwoFactorService twoFactorService,
                       SimpleRateLimiter rateLimiter) {
        this.usuarioRepository = usuarioRepository;
        this.authChallengeRepository = authChallengeRepository;
        this.tokenService = tokenService;
        this.twoFactorService = twoFactorService;
        this.rateLimiter = rateLimiter;
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request, String ip, String deviceInfo) {
        if (!rateLimiter.allow("login:" + ip + ":" + request.getUsernameOrEmail(), 10, 60)) {
            throw new IllegalArgumentException("Demasiados intentos de login");
        }

        Usuario user = usuarioRepository.findByPerfilUsuario(request.getUsernameOrEmail()).orElse(null);
        if (user == null || !request.getPassword().equals(user.getContrasenaUsuario())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            String challengeId = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(3);
            authChallengeRepository.save(AuthChallenge.builder()
                    .id(challengeId)
                    .userId(user.getIdUsuario())
                    .expiresAt(expiresAt)
                    .build());
            return AuthDtos.AuthResponse.builder()
                    .requires2fa(true)
                    .challengeId(challengeId)
                    .challengeExpiresAt(expiresAt)
                    .build();
        }

        return buildFinalAuthResponse(user, deviceInfo);
    }

    public AuthDtos.AuthResponse verifyTwoFactor(AuthDtos.TwoFactorVerifyRequest request, String ip, String deviceInfo) {
        if (!rateLimiter.allow("verify2fa:" + ip + ":" + request.getChallengeId(), 10, 60)) {
            throw new IllegalArgumentException("Demasiados intentos de verificación OTP");
        }

        AuthChallenge challenge = authChallengeRepository.findById(request.getChallengeId()).orElseThrow(() -> new IllegalArgumentException("Challenge inválido"));
        if (challenge.getConsumedAt() != null || challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Challenge expirado o ya consumido");
        }

        Usuario user = usuarioRepository.findById(challenge.getUserId()).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!twoFactorService.verifyForLogin(user, request.getOtpCode())) {
            throw new IllegalArgumentException("OTP inválido o reutilizado");
        }

        challenge.setConsumedAt(LocalDateTime.now());
        authChallengeRepository.save(challenge);

        return buildFinalAuthResponse(user, deviceInfo);
    }

    public AuthDtos.RefreshResponse refresh(AuthDtos.RefreshRequest request) {
        RefreshToken valid = tokenService.validateRefreshToken(request.getRefreshToken());
        if (valid == null) {
            throw new IllegalArgumentException("Refresh token inválido");
        }

        Usuario user = usuarioRepository.findById(valid.getUserId()).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        String newAccessToken = tokenService.generateAccessToken(user.getIdUsuario(), List.of("USER"));
        String rotatedRefresh = tokenService.rotateRefreshToken(request.getRefreshToken());

        return AuthDtos.RefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(rotatedRefresh)
                .build();
    }

    public void logout(AuthDtos.LogoutRequest request) {
        tokenService.revokeRefreshToken(request.getRefreshToken());
    }

    private AuthDtos.AuthResponse buildFinalAuthResponse(Usuario user, String deviceInfo) {
        String accessToken = tokenService.generateAccessToken(user.getIdUsuario(), List.of("USER"));
        String refreshToken = tokenService.issueRefreshToken(user.getIdUsuario(), deviceInfo);

        return AuthDtos.AuthResponse.builder()
                .requires2fa(false)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userInfo(AuthDtos.UserInfo.builder()
                        .userId(user.getIdUsuario())
                        .username(user.getPerfilUsuario())
                        .roles(List.of("USER"))
                        .twoFactorEnabled(Boolean.TRUE.equals(user.getTwoFactorEnabled()))
                        .build())
                .build();
    }
}
