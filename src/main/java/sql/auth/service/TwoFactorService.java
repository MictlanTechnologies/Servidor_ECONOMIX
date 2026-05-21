package sql.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sql.auth.dto.AuthDtos;
import sql.auth.exception.AuthExceptions.InvalidOtpException;
import sql.auth.exception.AuthExceptions.UnauthorizedAuthException;
import sql.auth.util.SecretCryptoService;
import sql.auth.util.TotpService;
import sql.model.Usuario;
import sql.repository.UsuarioRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TwoFactorService {

    private final TotpService totpService;
    private final SecretCryptoService secretCryptoService;
    private final UsuarioRepository usuarioRepository;

    public AuthDtos.TwoFactorSetupResponse setup(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedAuthException("Usuario no encontrado."));

        String secret = totpService.generateBase32Secret();
        user.setTwoFactorSecretEncrypted(secretCryptoService.encrypt(secret));
        user.setTwoFactorEnabled(false);
        user.setTwoFactorVerifiedAt(null);
        user.setLastOtpTimestepUsed(null);
        usuarioRepository.save(user);

        String issuer = "ECONOMIX";
        String label = issuer + ":" + user.getPerfilUsuario();
        String otpauthUri = "otpauth://totp/" + URLEncoder.encode(label, StandardCharsets.UTF_8)
                + "?secret=" + secret
                + "&issuer=" + URLEncoder.encode(issuer, StandardCharsets.UTF_8)
                + "&algorithm=SHA1&digits=6&period=30";

        return AuthDtos.TwoFactorSetupResponse.builder()
                .otpauthUri(otpauthUri)
                .secretMasked(maskSecret(secret))
                .build();
    }

    public AuthDtos.TwoFactorToggleResponse enable(Integer userId, String otpCode) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedAuthException("Usuario no encontrado."));
        String secret = decryptRequiredSecret(user);
        Long usedStep = totpService.resolveValidatedTimestep(secret, otpCode, 1);
        if (usedStep == null) {
            throw new InvalidOtpException("OTP inválido.");
        }
        user.setTwoFactorEnabled(true);
        user.setTwoFactorVerifiedAt(LocalDateTime.now());
        user.setLastOtpTimestepUsed(usedStep);
        usuarioRepository.save(user);

        return AuthDtos.TwoFactorToggleResponse.builder().twoFactorEnabled(true).build();
    }

    public AuthDtos.TwoFactorToggleResponse disable(Integer userId, String otpCode) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedAuthException("Usuario no encontrado."));
        String secret = decryptRequiredSecret(user);
        Long usedStep = totpService.resolveValidatedTimestep(secret, otpCode, 1);
        if (usedStep == null) {
            throw new InvalidOtpException("OTP inválido.");
        }
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecretEncrypted(null);
        user.setTwoFactorVerifiedAt(null);
        user.setLastOtpTimestepUsed(null);
        usuarioRepository.save(user);

        return AuthDtos.TwoFactorToggleResponse.builder().twoFactorEnabled(false).build();
    }

    public void verifyForLogin(Usuario user, String otpCode) {
        if (user == null || !Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            return;
        }
        String secret = decryptRequiredSecret(user);
        Long usedStep = totpService.resolveValidatedTimestep(secret, otpCode, 1);
        if (usedStep == null) {
            throw new InvalidOtpException("OTP inválido.");
        }
        if (user.getLastOtpTimestepUsed() != null && usedStep <= user.getLastOtpTimestepUsed()) {
            throw new InvalidOtpException("OTP ya utilizado.");
        }
        user.setLastOtpTimestepUsed(usedStep);
        user.setTwoFactorVerifiedAt(LocalDateTime.now());
        usuarioRepository.save(user);
    }

    public boolean isEnabledForUser(Integer userId) {
        if (userId == null) return false;
        return usuarioRepository.findById(userId)
                .map(Usuario::getTwoFactorEnabled)
                .orElse(false);
    }

    private String decryptRequiredSecret(Usuario user) {
        if (user.getTwoFactorSecretEncrypted() == null || user.getTwoFactorSecretEncrypted().isBlank()) {
            throw new InvalidOtpException("No hay configuración 2FA para el usuario.");
        }
        return secretCryptoService.decrypt(user.getTwoFactorSecretEncrypted());
    }

    private String maskSecret(String secret) {
        if (secret == null || secret.length() <= 8) {
            return "****";
        }
        return secret.substring(0, 4) + "****" + secret.substring(secret.length() - 4);
    }
}
