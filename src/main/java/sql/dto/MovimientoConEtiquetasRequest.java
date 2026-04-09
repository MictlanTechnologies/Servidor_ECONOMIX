package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoConEtiquetasRequest<T> {
    private T movimiento;
    private CategoriaDto categoria;
    private List<String> etiquetas;
}
