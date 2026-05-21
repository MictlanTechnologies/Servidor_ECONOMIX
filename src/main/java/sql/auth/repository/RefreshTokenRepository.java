package sql.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sql.auth.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
}
