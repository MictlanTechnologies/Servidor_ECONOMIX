package sql.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sql.auth.dto.AuthDtos;
import sql.auth.exception.AuthExceptions.BadRequestAuthException;
import sql.auth.exception.AuthExceptions.UnauthorizedAuthException;
import sql.auth.model.AuthChallenge;
import sql.auth.repository.AuthChallengeRepository;
import sql.model.Usuario;
import sql.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final TwoFactorService twoFactorService;
    private final AuthChallengeRepository authChallengeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        Usuario user = usuarioRepository.findByPerfilUsuario(request.getUsernameOrEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas."));

        String raw = request.getPassword();
        String stored = user.getContrasenaUsuario();

        boolean ok = stored != null && (stored.equals(raw) || (stored.startsWith("$2") && passwordEncoder.matches(raw, stored)));
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas.");
        }

        boolean twoFactorEnabled = twoFactorService.isEnabledForUser(user.getIdUsuario());
        if (twoFactorEnabled) {
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
            String challengeId = UUID.randomUUID().toString();
            authChallengeRepository.save(AuthChallenge.builder()
                    .challengeId(challengeId)
                    .idUsuario(user.getIdUsuario())
                    .expiresAt(expiresAt)
                    .used(false)
                    .build());

            return AuthDtos.LoginResponse.builder()
                    .requires2fa(true)
                    .challengeId(challengeId)
                    .challengeExpiresAt(expiresAt)
                    .userInfo(AuthDtos.UserInfo.builder()
                            .userId(user.getIdUsuario())
                            .username(user.getPerfilUsuario())
                            .roles(List.of("USER"))
                            .twoFactorEnabled(true)
                            .build())
                    .build();
        }

        return AuthDtos.LoginResponse.builder()
                .requires2fa(false)
                .accessToken(tokenService.generateAccessToken(user.getIdUsuario()))
                .refreshToken(tokenService.issueRefreshToken(user.getIdUsuario()))
                .userInfo(AuthDtos.UserInfo.builder()
                        .userId(user.getIdUsuario())
                        .username(user.getPerfilUsuario())
                        .roles(List.of("USER"))
                        .twoFactorEnabled(false)
                        .build())
                .build();
    }

    public AuthDtos.LoginResponse verify2fa(AuthDtos.Verify2FARequest request) {
        if (request == null || request.getChallengeId() == null || request.getChallengeId().isBlank() || request.resolvedCode() == null || request.resolvedCode().isBlank()) {
            throw new BadRequestAuthException("challengeId y code son obligatorios.");
        }

        AuthChallenge challenge = authChallengeRepository.findByChallengeId(request.getChallengeId())
                .orElseThrow(() -> new UnauthorizedAuthException("Challenge inválido."));

        if (Boolean.TRUE.equals(challenge.getUsed()) || challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedAuthException("Challenge expirado o usado.");
        }

        Usuario user = usuarioRepository.findById(challenge.getIdUsuario())
                .orElseThrow(() -> new UnauthorizedAuthException("Usuario no encontrado."));

        twoFactorService.verifyForLogin(user, request.resolvedCode());
        challenge.setUsed(true);
        authChallengeRepository.save(challenge);

        return AuthDtos.LoginResponse.builder()
                .requires2fa(false)
                .accessToken(tokenService.generateAccessToken(user.getIdUsuario()))
                .refreshToken(tokenService.issueRefreshToken(user.getIdUsuario()))
                .userInfo(AuthDtos.UserInfo.builder()
                        .userId(user.getIdUsuario())
                        .username(user.getPerfilUsuario())
                        .roles(List.of("USER"))
                        .twoFactorEnabled(true)
                        .build())
                .build();
    }

    public AuthDtos.RefreshResponse refresh(AuthDtos.RefreshRequest request) {
        var rt = tokenService.validateRefreshToken(request.getRefreshToken());
        tokenService.revokeByToken(request.getRefreshToken());
        return AuthDtos.RefreshResponse.builder()
                .accessToken(tokenService.generateAccessToken(rt.getIdUsuario()))
                .refreshToken(tokenService.issueRefreshToken(rt.getIdUsuario()))
                .build();
    }

    public void logout(String refreshToken) {
        tokenService.revokeByToken(refreshToken);
    }
}
