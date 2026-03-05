package sql.auth.util;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;

@Component
public class TotpService {
    private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int DIGITS = 6;
    private static final long TIMESTEP_SECONDS = 30L;
    private final SecureRandom random = new SecureRandom();

    public String generateBase32Secret() {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return base32Encode(bytes);
    }

    public boolean verifyWithWindow(String secretBase32, String otpCode, int window, Long lastUsedTimestep, long nowEpochSeconds) {
        if (otpCode == null || !otpCode.matches("\\d{6}")) {
            return false;
        }
        long currentStep = nowEpochSeconds / TIMESTEP_SECONDS;
        for (int i = -window; i <= window; i++) {
            long step = currentStep + i;
            if (lastUsedTimestep != null && step <= lastUsedTimestep) {
                continue;
            }
            String expected = generateCode(secretBase32, step);
            if (expected.equals(otpCode)) {
                return true;
            }
        }
        return false;
    }

    public long resolveValidatedStep(String secretBase32, String otpCode, int window, long nowEpochSeconds) {
        long currentStep = nowEpochSeconds / TIMESTEP_SECONDS;
        for (int i = -window; i <= window; i++) {
            long step = currentStep + i;
            if (generateCode(secretBase32, step).equals(otpCode)) {
                return step;
            }
        }
        return -1;
    }

    public String generateCodeNow(String secretBase32) {
        return generateCode(secretBase32, Instant.now().getEpochSecond() / TIMESTEP_SECONDS);
    }

    private String generateCode(String secretBase32, long timestep) {
        try {
            byte[] key = base32Decode(secretBase32);
            byte[] data = ByteBuffer.allocate(8).putLong(timestep).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 0xF;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            int otp = binary % 1_000_000;
            return String.format("%06d", otp);
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot calculate TOTP", ex);
        }
    }

    private String base32Encode(byte[] data) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        int currByte;
        int digit;
        for (int i = 0; i < data.length; ) {
            currByte = data[i] >= 0 ? data[i] : (data[i] + 256);
            if (index > 3) {
                int nextByte;
                if ((i + 1) < data.length) {
                    nextByte = data[i + 1] >= 0 ? data[i + 1] : (data[i + 1] + 256);
                } else {
                    nextByte = 0;
                }
                digit = currByte & (0xFF >> index);
                index = (index + 5) % 8;
                digit <<= index;
                digit |= nextByte >> (8 - index);
                i++;
            } else {
                digit = (currByte >> (8 - (index + 5))) & 0x1F;
                index = (index + 5) % 8;
                if (index == 0) {
                    i++;
                }
            }
            result.append(BASE32_ALPHABET.charAt(digit));
        }
        return result.toString();
    }

    private byte[] base32Decode(String base32) {
        String normalized = base32.replace("=", "").trim().toUpperCase();
        byte[] bytes = new byte[normalized.length() * 5 / 8];
        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;
        for (char c : normalized.toCharArray()) {
            int val = BASE32_ALPHABET.indexOf(c);
            if (val < 0) {
                throw new IllegalArgumentException("Invalid base32 value");
            }
            buffer <<= 5;
            buffer |= val & 31;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                bytes[count++] = (byte) ((buffer >> (bitsLeft - 8)) & 255);
                bitsLeft -= 8;
            }
        }
        return count == bytes.length ? bytes : Arrays.copyOf(bytes, count);
    }
}
