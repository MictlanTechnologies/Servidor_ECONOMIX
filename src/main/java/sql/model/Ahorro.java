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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_ahorro")
public class Ahorro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAhorro")
    private Integer idAhorro;

    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "nombreObjetivo", length = 120)
    private String nombreObjetivo;

    @Column(name = "descripcionObjetivo")
    private String descripcionObjetivo;

    @Column(name = "meta")
    private BigDecimal meta;

    @Column(name = "montoAhorrado")
    private BigDecimal montoAhorrado;

    @Column(name = "fechaLimite")
    private LocalDate fechaLimite;

    @Column(name = "fechaAhorro")
    private LocalDate fechaAhorro;

    @Column(name = "fechaActualizaciónA")
    private LocalDate fechaActualizacionA;

    @Column(name = "periodoTAhorro", length = 50)
    private String periodoTAhorro;

    @Column(name = "montoAhorro")
    private BigDecimal montoAhorro;

    @Column(name = "idIngresos")
    private Integer idIngresos;
}
