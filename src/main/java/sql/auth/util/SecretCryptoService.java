package sql.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class SecretCryptoService {

    private final SecretKeySpec keySpec;

    public SecretCryptoService(@Value("${economix.2fa.key:change-this-2fa-key}") String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            this.keySpec = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo inicializar cifrado 2FA", e);
        }
    }

    public String encrypt(String value) {
        try {
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(128, iv));
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(encrypted, 0, payload, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cifrar secreto 2FA", e);
        }
    }

    public String decrypt(String value) {
        try {
            byte[] payload = Base64.getDecoder().decode(value);
            byte[] iv = new byte[12];
            byte[] encrypted = new byte[payload.length - 12];
            System.arraycopy(payload, 0, iv, 0, 12);
            System.arraycopy(payload, 12, encrypted, 0, encrypted.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(128, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo descifrar secreto 2FA", e);
        }
    }
}
