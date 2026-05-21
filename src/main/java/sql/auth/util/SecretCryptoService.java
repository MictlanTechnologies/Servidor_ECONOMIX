package sql.auth.util;

import org.springframework.stereotype.Service;

@Service
public class SecretCryptoService {
    public String encrypt(String value) { return value; }
    public String decrypt(String value) { return value; }
}
