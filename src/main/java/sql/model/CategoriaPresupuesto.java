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
@Table(name = "categoria_presupuesto")
public class CategoriaPresupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(name = "color_hex", length = 20)
    private String colorHex;

    @Column(name = "icon_key", length = 80)
    private String iconKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
