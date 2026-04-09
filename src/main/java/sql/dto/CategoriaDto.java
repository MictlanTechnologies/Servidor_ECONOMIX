package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sql.model.TipoCategoria;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDto {
    private Integer idCategoria;
    private Integer idUsuario;
    private TipoCategoria tipo;
    private String nombre;
    private String descripcion;
    private String color;
}
