package sql.ai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatbotMessageRequest {
    @NotNull
    @Min(1)
    private Integer idUsuario;

    @NotBlank
    @Max(1200)
    private String mensaje;

    private String contextoOpcional;
}
