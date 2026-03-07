package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaPresupuestoDto {
    private Integer id;
    private Integer usuarioId;
    private String nombre;
    private String colorHex;
    private String iconKey;
}
