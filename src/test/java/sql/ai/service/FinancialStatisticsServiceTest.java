package sql.ai.service;

import org.junit.jupiter.api.Test;
import sql.ai.analytics.FinancialStatisticsService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinancialStatisticsServiceTest {
    private final FinancialStatisticsService service = new FinancialStatisticsService();

    @Test void casoSinDatos() {
        assertTrue(service.analyze(List.of(BigDecimal.TEN), List.of(), BigDecimal.ONE, BigDecimal.TEN).isDatosInsuficientes());
    }

    @Test void detectaAtipicoYRiesgo() {
        var r = service.analyze(List.of(new BigDecimal("10"),new BigDecimal("10"),new BigDecimal("100"),new BigDecimal("10")), List.of(), new BigDecimal("95"), new BigDecimal("100"));
        assertEquals("ALTO", r.getNivelRiesgoFinanciero());
        assertNotNull(r.getMediaGasto());
    }
}
