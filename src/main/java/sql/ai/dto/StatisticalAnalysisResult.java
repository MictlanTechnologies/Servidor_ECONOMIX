package sql.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StatisticalAnalysisResult {
    private BigDecimal mediaGasto;
    private BigDecimal medianaGasto;
    private BigDecimal desviacionEstandarGasto;
    private Integer cantidadGastosAtipicos;
    private BigDecimal gastoProyectadoSiguientePeriodo;
    private String tendenciaIngresos;
    private String tendenciaGastos;
    private String nivelRiesgoFinanciero;
    private boolean datosInsuficientes;
}
