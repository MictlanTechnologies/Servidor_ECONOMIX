package ai.service;

import org.junit.jupiter.api.Test;
import sql.model.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForecastServiceTest {

    private final ForecastService service = new ForecastService();

    @Test
    void forecastSpendShouldReturnPositiveRange() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 20);
        List<Gasto> gastos = List.of(
                gasto(1, from, "50"), gasto(2, from.plusDays(1), "55"), gasto(3, from.plusDays(2), "48"),
                gasto(4, from.plusDays(3), "52"), gasto(5, from.plusDays(4), "62"), gasto(6, from.plusDays(5), "45"),
                gasto(7, from.plusDays(6), "47"), gasto(8, from.plusDays(7), "49"), gasto(9, from.plusDays(8), "53"),
                gasto(10, from.plusDays(9), "60")
        );

        ForecastService.ForecastResult result = service.forecastSpend(gastos, from, to, 7);
        assertTrue(result.expected().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.upper95().compareTo(result.lower95()) >= 0);
    }

    private Gasto gasto(int id, LocalDate date, String amount) {
        return Gasto.builder().idGastos(id).idUsuario(1).fechaGastos(date).montoGasto(new BigDecimal(amount)).build();
    }
}
