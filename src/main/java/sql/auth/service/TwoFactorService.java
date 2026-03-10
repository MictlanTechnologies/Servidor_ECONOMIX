package sql.auth.service;

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
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class TwoFactorService {

    private final TotpService totpService;
    private final SecretCryptoService secretCryptoService;
    private final UsuarioRepository usuarioRepository;

    public TwoFactorService(TotpService totpService, SecretCryptoService secretCryptoService, UsuarioRepository usuarioRepository) {
        this.totpService = totpService;
        this.secretCryptoService = secretCryptoService;
        this.usuarioRepository = usuarioRepository;
    }

    public AuthDtos.TwoFactorSetupResponse setup(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedAuthException("Usuario no autorizado"));

        String secret = totpService.generateBase32Secret();
        user.setTwoFactorSecretEncrypted(secretCryptoService.encrypt(secret));
        user.setTwoFactorEnabled(false);
        usuarioRepository.save(user);

        String issuer = "ECONOMIX";
        String account = user.getPerfilUsuario();
        String label = urlEncode(issuer + ":" + account);
        String otpauthUri = "otpauth://totp/" + label
                + "?secret=" + secret
                + "&issuer=" + urlEncode(issuer)
                + "&algorithm=SHA1&digits=6&period=30";

        String secretMasked = secret.substring(0, 4) + "****" + secret.substring(secret.length() - 4);
        return AuthDtos.TwoFactorSetupResponse.builder()
                .otpauthUri(otpauthUri)
                .secretMasked(secretMasked)
                .build();
    }

    public AuthDtos.TwoFactorToggleResponse enable(Integer userId, String otpCode) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedAuthException("Usuario no autorizado"));

        String decrypted = decryptExisting(user);
        long now = Instant.now().getEpochSecond();
        boolean valid = totpService.verifyWithWindow(decrypted, otpCode, 1, user.getLastOtpTimestepUsed(), now);
        if (!valid) {
            throw new InvalidOtpException("OTP inválido");
        }

        long step = totpService.resolveValidatedStep(decrypted, otpCode, 1, now);
        user.setLastOtpTimestepUsed(step);
        user.setTwoFactorEnabled(true);
        user.setTwoFactorVerifiedAt(LocalDateTime.now());
        usuarioRepository.save(user);
        return AuthDtos.TwoFactorToggleResponse.builder().twoFactorEnabled(true).build();
    }

    public AuthDtos.TwoFactorToggleResponse disable(Integer userId, String otpCode) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedAuthException("Usuario no autorizado"));

        String decrypted = decryptExisting(user);
        long now = Instant.now().getEpochSecond();
        boolean valid = totpService.verifyWithWindow(decrypted, otpCode, 1, user.getLastOtpTimestepUsed(), now);
        if (!valid) {
            throw new InvalidOtpException("OTP inválido");
        }

        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecretEncrypted(null);
        user.setTwoFactorVerifiedAt(null);
        user.setLastOtpTimestepUsed(null);
        usuarioRepository.save(user);
        return AuthDtos.TwoFactorToggleResponse.builder().twoFactorEnabled(false).build();
    }

    public boolean verifyForLogin(Usuario user, String otpCode) {
        String secret = decryptExisting(user);
        long now = Instant.now().getEpochSecond();
        boolean valid = totpService.verifyWithWindow(secret, otpCode, 1, user.getLastOtpTimestepUsed(), now);
        if (!valid) {
            return false;
        }
        long step = totpService.resolveValidatedStep(secret, otpCode, 1, now);
        user.setLastOtpTimestepUsed(step);
        usuarioRepository.save(user);
        return true;
    }

    private String decryptExisting(Usuario user) {
        if (user.getTwoFactorSecretEncrypted() == null || user.getTwoFactorSecretEncrypted().isBlank()) {
            throw new InvalidOtpException("No hay secreto 2FA configurado");
        }
        return secretCryptoService.decrypt(user.getTwoFactorSecretEncrypted());
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
