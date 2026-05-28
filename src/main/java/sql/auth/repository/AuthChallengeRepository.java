package sql.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sql.auth.model.AuthChallenge;

import java.util.Optional;

public interface AuthChallengeRepository extends JpaRepository<AuthChallenge, Integer> {
    Optional<AuthChallenge> findByChallengeId(String challengeId);
}
