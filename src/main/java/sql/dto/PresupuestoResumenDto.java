package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresupuestoResumenDto {
    private Integer categoriaId;
    private String nombreCategoria;
    private String colorHex;
    private String iconKey;
    private BigDecimal asignado;
    private BigDecimal gastado;
    private BigDecimal disponible;
}
