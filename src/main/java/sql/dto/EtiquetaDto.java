package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtiquetaDto {
    private Integer idEtiqueta;
    private Integer idUsuario;
    private String nombre;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
