package sql.ai.analytics;

import org.springframework.stereotype.Service;
import sql.ai.dto.StatisticalAnalysisResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class FinancialStatisticsService {
    public StatisticalAnalysisResult analyze(List<BigDecimal> gastos, List<BigDecimal> ingresos, BigDecimal gastoActual, BigDecimal presupuestoActual) {
        if (gastos == null || gastos.size() < 2) {
            return StatisticalAnalysisResult.builder().nivelRiesgoFinanciero("SIN_DATOS").datosInsuficientes(true).build();
        }
        List<Double> vals = gastos.stream().filter(Objects::nonNull).map(BigDecimal::doubleValue).sorted().toList();
        double media = vals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double mediana = vals.size() % 2 == 0 ? (vals.get(vals.size()/2-1)+vals.get(vals.size()/2))/2 : vals.get(vals.size()/2);
        double var = vals.stream().mapToDouble(v -> Math.pow(v-media,2)).average().orElse(0);
        double sd = Math.sqrt(var);
        long outliers = vals.stream().filter(v -> v > media + 2*sd).count();
        BigDecimal proj = vals.size() >= 3 ? BigDecimal.valueOf(vals.subList(vals.size()-3, vals.size()).stream().mapToDouble(Double::doubleValue).average().orElse(media)) : BigDecimal.valueOf(media);
        return StatisticalAnalysisResult.builder()
                .mediaGasto(BigDecimal.valueOf(media).setScale(2, RoundingMode.HALF_UP))
                .medianaGasto(BigDecimal.valueOf(mediana).setScale(2, RoundingMode.HALF_UP))
                .desviacionEstandarGasto(BigDecimal.valueOf(sd).setScale(2, RoundingMode.HALF_UP))
                .cantidadGastosAtipicos((int) outliers)
                .gastoProyectadoSiguientePeriodo(proj.setScale(2, RoundingMode.HALF_UP))
                .tendenciaIngresos("Comparación contra historial propio")
                .tendenciaGastos("Comparación contra historial propio")
                .nivelRiesgoFinanciero(riesgo(gastoActual, presupuestoActual))
                .datosInsuficientes(false)
                .build();
    }

    private String riesgo(BigDecimal gasto, BigDecimal presupuesto) {
        if (gasto == null || presupuesto == null || presupuesto.compareTo(BigDecimal.ZERO)<=0) return "SIN_DATOS";
        BigDecimal pct = gasto.divide(presupuesto, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        if (pct.compareTo(BigDecimal.valueOf(70)) <= 0) return "BAJO";
        if (pct.compareTo(BigDecimal.valueOf(90)) <= 0) return "MEDIO";
        if (pct.compareTo(BigDecimal.valueOf(100)) <= 0) return "ALTO";
        return "CRITICO";
    }
}
