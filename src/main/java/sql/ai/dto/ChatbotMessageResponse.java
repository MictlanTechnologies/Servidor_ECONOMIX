package sql.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ChatbotMessageResponse {
    private String respuesta;
    private Map<String, Object> resumenFinancieroUsado;
    private List<String> recomendaciones;
    private List<String> alertas;
    private String nivelRiesgoFinanciero;
    private boolean datosInsuficientes;
    private List<String> accionesSugeridas;
    private String disclaimer;
}
