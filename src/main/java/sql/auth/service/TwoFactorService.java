package sql.auth.service;

import org.springframework.stereotype.Service;

@Service
public class TwoFactorService {
    public boolean isEnabledForUser(Integer userId) {
        return false;
    }
}
