package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtiquetaDto {
    private Integer idEtiqueta;
    private Integer idUsuario;
    private String nombre;
    private String slug;
}
