package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request para guardar un ingreso con etiquetas asociadas.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngresoWithTagsRequest {
    @NotNull
    private IngresoDto movimiento;

    private List<String> etiquetas;
}
