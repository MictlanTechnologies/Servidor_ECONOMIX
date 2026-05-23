package sql.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class FinancialContextSummary {
    private BigDecimal ingresoTotalPeriodo;
    private BigDecimal gastoTotalPeriodo;
    private BigDecimal balanceNeto;
    private BigDecimal porcentajeAhorro;
    private Map<String, BigDecimal> gastoPorCategoria;
    private List<String> categoriasMayorGasto;
    private List<String> presupuestosExcedidos;
    private List<String> presupuestosCercaLimite;
    private String tendenciaGasto;
    private String frecuenciaGastos;
    private String variacionRespectoPeriodosAnteriores;
    private BigDecimal totalAhorros;
    private boolean datosInsuficientes;
}
