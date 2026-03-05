package sql.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_auth_challenge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthChallenge {
    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "idUsuario", nullable = false)
    private Integer userId;

    @Column(name = "expiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "consumedAt")
    private LocalDateTime consumedAt;
}
