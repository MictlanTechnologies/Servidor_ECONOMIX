package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request para guardar un gasto con etiquetas asociadas.
 * Requiere:
 * - movimiento: los datos del gasto
 * - etiquetas: lista de nombres de etiquetas (se crean automáticamente si no existen)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GastoWithTagsRequest {
    @NotNull
    private GastoDto movimiento;

    private List<String> etiquetas; // ej: ["#comida", "#urgente"]
}
