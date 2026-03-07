package sql.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AsignacionRequestDto {
    @NotNull
    private Integer usuarioId;
    @NotNull
    private Integer ingresoId;
    @NotNull
    private Integer categoriaId;
    @NotNull
    private BigDecimal monto;
    @NotNull
    private LocalDate fecha;
}
