package sql.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sql.auth.dto.AuthDtos;
import sql.model.Usuario;
import sql.repository.UsuarioRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final TwoFactorService twoFactorService;
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

        return AuthDtos.LoginResponse.builder()
                .requires2fa(false)
                .accessToken(tokenService.generateAccessToken(user.getIdUsuario()))
                .refreshToken(tokenService.issueRefreshToken(user.getIdUsuario()))
                .userInfo(AuthDtos.UserInfo.builder()
                        .userId(user.getIdUsuario())
                        .username(user.getPerfilUsuario())
                        .roles(List.of("USER"))
                        .twoFactorEnabled(twoFactorService.isEnabledForUser(user.getIdUsuario()))
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

    public void logout(String refreshToken) { tokenService.revokeByToken(refreshToken); }
}
