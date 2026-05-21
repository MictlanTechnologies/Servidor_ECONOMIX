package sql.auth.util;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class TotpService {

    private static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int DIGITS = 6;
    private static final int PERIOD_SECONDS = 30;

    public String generateBase32Secret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return toBase32(bytes);
    }

    public boolean verifyWithWindow(String base32Secret, String otpCode, int window) {
        return resolveValidatedTimestep(base32Secret, otpCode, window) != null;
    }

    public Long resolveValidatedTimestep(String base32Secret, String otpCode, int window) {
        if (otpCode == null || !otpCode.matches("\\d{6}")) {
            return null;
        }
        long nowStep = Instant.now().getEpochSecond() / PERIOD_SECONDS;
        for (int i = -window; i <= window; i++) {
            long step = nowStep + i;
            if (generateTotp(base32Secret, step).equals(otpCode)) {
                return step;
            }
        }
        return null;
    }

    private String generateTotp(String base32Secret, long timestep) {
        try {
            byte[] key = fromBase32(base32Secret);
            byte[] data = ByteBuffer.allocate(8).putLong(timestep).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            int otp = binary % (int) Math.pow(10, DIGITS);
            return String.format("%06d", otp);
        } catch (Exception e) {
            return "";
        }
    }

    private String toBase32(byte[] data) {
        StringBuilder result = new StringBuilder();
        int buffer = data[0];
        int next = 1;
        int bitsLeft = 8;
        while (bitsLeft > 0 || next < data.length) {
            if (bitsLeft < 5) {
                if (next < data.length) {
                    buffer <<= 8;
                    buffer |= (data[next++] & 0xff);
                    bitsLeft += 8;
                } else {
                    int pad = 5 - bitsLeft;
                    buffer <<= pad;
                    bitsLeft += pad;
                }
            }
            int index = 0x1f & (buffer >> (bitsLeft - 5));
            bitsLeft -= 5;
            result.append(BASE32.charAt(index));
        }
        return result.toString();
    }

    private byte[] fromBase32(String base32) {
        base32 = base32.replace("=", "").toUpperCase();
        byte[] bytes = new byte[base32.length() * 5 / 8];
        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;
        for (char c : base32.toCharArray()) {
            int val = BASE32.indexOf(c);
            if (val < 0) continue;
            buffer <<= 5;
            buffer |= val & 31;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                bytes[count++] = (byte) ((buffer >> (bitsLeft - 8)) & 255);
                bitsLeft -= 8;
            }
        }
        if (count == bytes.length) return bytes;
        byte[] out = new byte[count];
        System.arraycopy(bytes, 0, out, 0, count);
        return out;
    }
}
