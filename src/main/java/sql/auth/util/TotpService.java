package sql.auth.util;

import org.springframework.stereotype.Service;

@Service
public class TotpService {
    public boolean verifyCode(String secret, String code) {
        return code != null && code.matches("\\d{6}");
    }
}
