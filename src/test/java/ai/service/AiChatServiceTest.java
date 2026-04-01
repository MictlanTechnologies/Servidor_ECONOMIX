package ai.service;

import ai.dto.AiChatRequest;
import ai.dto.AiChatResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sql.model.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

class AiChatServiceTest {

    @Test
    void shouldFallbackWhenProviderUnavailable() {
        AIDataService dataService = Mockito.mock(AIDataService.class);
        ForecastService forecastService = new ForecastService();
        GeminiClient geminiClient = Mockito.mock(GeminiClient.class);

        Mockito.when(dataService.getGastos(eq(1), any(), any(), isNull())).thenReturn(List.of(
                gasto(1, "10"), gasto(2, "11"), gasto(3, "12"), gasto(4, "13"), gasto(5, "9"),
                gasto(6, "10"), gasto(7, "12"), gasto(8, "11"), gasto(9, "9"), gasto(10, "10")
        ));
        Mockito.when(geminiClient.generate(anyString(), anyString())).thenReturn(null);

        AiChatService service = new AiChatService(dataService, forecastService, geminiClient);
        AiChatResponse response = service.chat(1, AiChatRequest.builder().message("resume mis gastos").build());

        assertEquals("PROVIDER_UNAVAILABLE", response.getStatus());
    }

    private Gasto gasto(int day, String amount) {
        return Gasto.builder().idUsuario(1).fechaGastos(LocalDate.of(2026, 1, day)).montoGasto(new BigDecimal(amount)).build();
    }
}
