package sql.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.auth.model.AuthChallenge;

@Repository
public interface AuthChallengeRepository extends JpaRepository<AuthChallenge, String> {
}
