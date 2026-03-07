package sql.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearCategoriaRequest {
    @NotNull
    private Integer usuarioId;

    @NotBlank
    private String nombre;

    private String colorHex;
    private String iconKey;
}
