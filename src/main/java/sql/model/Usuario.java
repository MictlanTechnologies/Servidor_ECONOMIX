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

    @Column(name = "twoFactorEnabled")
    private Boolean twoFactorEnabled;

    @Column(name = "twoFactorSecretEncrypted", length = 1000)
    private String twoFactorSecretEncrypted;

    @Column(name = "twoFactorVerifiedAt")
    private LocalDateTime twoFactorVerifiedAt;

    @Column(name = "lastOtpTimestepUsed")
    private Long lastOtpTimestepUsed;
}
