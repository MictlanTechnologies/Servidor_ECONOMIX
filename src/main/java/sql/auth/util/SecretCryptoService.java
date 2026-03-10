package sql.auth.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecretCryptoService {

    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String configuredKey;

    private SecretKeySpec key;

    public SecretCryptoService(@Value("${economix.2fa.key:}") String configuredKey) {
        this.configuredKey = configuredKey;
    }

    @PostConstruct
    public void init() {
        if (configuredKey == null || configuredKey.isBlank()) {
            throw new IllegalStateException("Missing required property economix.2fa.key (env: ECONOMIX_2FA_KEY)");
        }

        byte[] rawUtf8 = configuredKey.getBytes(StandardCharsets.UTF_8);
        if (rawUtf8.length == 16 || rawUtf8.length == 24 || rawUtf8.length == 32) {
            this.key = new SecretKeySpec(rawUtf8, "AES");
            return;
        }

        byte[] decodedBase64;
        try {
            decodedBase64 = Base64.getDecoder().decode(configuredKey);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("economix.2fa.key must be 16/24/32 bytes (or base64) for AES");
        }

        if (decodedBase64.length != 16 && decodedBase64.length != 24 && decodedBase64.length != 32) {
            throw new IllegalStateException("economix.2fa.key must be 16/24/32 bytes (or base64) for AES");
        }
        this.key = new SecretKeySpec(decodedBase64, "AES");
    }

    public String encrypt(String plainText) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(cipherText, 0, payload, iv.length, cipherText.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to encrypt 2FA secret", ex);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            byte[] payload = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[IV_LENGTH];
            byte[] cipherText = new byte[payload.length - IV_LENGTH];
            System.arraycopy(payload, 0, iv, 0, IV_LENGTH);
            System.arraycopy(payload, IV_LENGTH, cipherText, 0, cipherText.length);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to decrypt 2FA secret", ex);
        }
    }
}
