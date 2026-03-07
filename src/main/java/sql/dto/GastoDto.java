package sql.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO alineado a la app Android.
 *
 * En MySQL algunos nombres de columna llevan acentos (descripciónGasto, artículoGasto),
 * por eso aceptamos ambas variantes en JSON con @JsonAlias.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GastoDto {
    private Integer idGastos;
    private Integer idUsuario;

    @JsonAlias({"descripciónGasto"})
    private String descripcionGasto;

    @JsonAlias({"artículoGasto"})
    private String articuloGasto;

    private BigDecimal montoGasto;
    private LocalDate fechaGastos;
    private String periodoGastos;

    @JsonAlias({"categoriaId"})
    private Integer idCategoriaPresupuesto;
}
