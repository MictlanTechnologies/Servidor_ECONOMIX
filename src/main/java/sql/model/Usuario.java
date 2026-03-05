package sql.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla tbl_usuario.
 *
 * Importante: la BD usa el nombre de columna "contraseñaUsuario".
 * Por compatibilidad con Java/JSON usamos el campo contrasenaUsuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "perfilUsuario", nullable = false, length = 50)
    private String perfilUsuario;

    @Column(name = "contraseñaUsuario", nullable = false, length = 100)
    private String contrasenaUsuario;

    @Column(name = "twoFactorEnabled", nullable = false)
    @Builder.Default
    private Boolean twoFactorEnabled = false;

    @Column(name = "twoFactorSecretEncrypted", length = 512)
    private String twoFactorSecretEncrypted;

    @Column(name = "twoFactorVerifiedAt")
    private LocalDateTime twoFactorVerifiedAt;

    @Column(name = "lastOtpTimestepUsed")
    private Long lastOtpTimestepUsed;
}
