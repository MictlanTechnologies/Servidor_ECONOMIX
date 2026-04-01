package sql.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AhorroDto {
    private Integer idAhorro;
    private Integer idUsuario;
    private String nombreObjetivo;
    private String descripcionObjetivo;
    private BigDecimal meta;
    private BigDecimal montoAhorrado;
    private LocalDate fechaLimite;

    private Integer idIngresos;
    private BigDecimal montoAhorro;
    private String periodoTAhorro;
    private LocalDate fechaAhorro;

    @JsonAlias({"fechaActualizaciónA"})
    private LocalDate fechaActualizacionA;
}
