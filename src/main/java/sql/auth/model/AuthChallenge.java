package sql.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_challenge")
public class AuthChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAuthChallenge")
    private Integer idAuthChallenge;

    @Column(name = "challengeId", nullable = false, unique = true, length = 120)
    private String challengeId;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "expiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private Boolean used;
}
