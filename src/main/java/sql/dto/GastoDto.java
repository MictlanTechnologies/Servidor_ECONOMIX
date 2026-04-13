package sql.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
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

    @NotNull(message = "idUsuario es obligatorio")
    private Integer idUsuario;

    @NotBlank(message = "descripcionGasto es obligatorio")
    @JsonAlias({"descripciónGasto"})
    private String descripcionGasto;

    @NotBlank(message = "articuloGasto es obligatorio")
    @JsonAlias({"artículoGasto"})
    private String articuloGasto;

    @NotNull(message = "montoGasto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto del gasto debe ser mayor que cero")
    private BigDecimal montoGasto;

    @NotNull(message = "fechaGastos es obligatorio")
    private LocalDate fechaGastos;

    @NotBlank(message = "periodoGastos es obligatorio")
    private String periodoGastos;

    @JsonAlias({"categoriaId"})
    private Integer idCategoriaPresupuesto;
}
