package sql.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_refresh_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idUsuario", nullable = false)
    private Integer userId;

    @Column(name = "tokenHash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revokedAt")
    private LocalDateTime revokedAt;

    @Column(name = "deviceInfo", length = 255)
    private String deviceInfo;
}
