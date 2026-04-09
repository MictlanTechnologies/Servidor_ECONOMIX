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
 * Entidad JPA para la tabla tbl_gastos.
 *
 * Importante: en el script SQL del proyecto, algunas columnas usan acentos:
 *  - `descripciónGasto`
 *  - `artículoGasto`
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_gastos")
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idGastos")
    private Integer idGastos;

    @Column(name = "descripciónGasto", nullable = false)
    private String descripcionGasto;

    @Column(name = "artículoGasto", nullable = false, length = 100)
    private String articuloGasto;

    @Column(name = "montoGasto", nullable = false)
    private BigDecimal montoGasto;

    @Column(name = "fechaGastos", nullable = false)
    private LocalDate fechaGastos;

    @Column(name = "periodoGastos", nullable = false, length = 50)
    private String periodoGastos;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "idCategoria")
    private Integer idCategoria;
}
