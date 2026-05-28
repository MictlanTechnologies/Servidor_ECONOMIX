package sql.ai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatbotMessageRequest {
    @NotNull
    @Min(1)
    private Integer idUsuario;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 1200, message = "El mensaje no puede superar los 1200 caracteres")
    private String mensaje;

    private String contextoOpcional;
}
