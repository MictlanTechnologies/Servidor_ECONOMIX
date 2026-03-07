package sql.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCategoriaRequest {
    @NotNull
    private Integer usuarioId;
    private String nombre;
    private String colorHex;
    private String iconKey;
}
