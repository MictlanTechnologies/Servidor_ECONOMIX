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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad JPA para la tabla tbl_ingresos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_ingresos")
public class Ingreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idIngresos")
    private Integer idIngresos;

    @Column(name = "montoIngreso")
    private BigDecimal montoIngreso;

    @Column(name = "periodicidadIngreso", length = 50)
    private String periodicidadIngreso;

    @Column(name = "fechaIngresos")
    private LocalDate fechaIngresos;

    @Column(name = "descripcionIngreso")
    private String descripcionIngreso;

    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "idCategoria")
    private Integer idCategoria;
}
