package ai.service;

import ai.dto.AiChatRequest;
import ai.dto.AiChatResponse;
import org.springframework.stereotype.Service;
import sql.model.Gasto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AiChatService {

    private final AIDataService aiDataService;
    private final ForecastService forecastService;
    private final GeminiClient geminiClient;

    public AiChatService(AIDataService aiDataService, ForecastService forecastService, GeminiClient geminiClient) {
        this.aiDataService = aiDataService;
        this.forecastService = forecastService;
        this.geminiClient = geminiClient;
    }

    public AiChatResponse chat(Integer userId, AiChatRequest request) {
        LocalDate to = request.getTo() == null ? LocalDate.now() : request.getTo();
        LocalDate from = request.getFrom() == null ? to.minusDays(30) : request.getFrom();
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from no puede ser mayor a to");
        }

        int horizonDays = request.getHorizonDays() == null ? 7 : Math.max(1, Math.min(60, request.getHorizonDays()));
        List<Gasto> gastos = aiDataService.getGastos(userId, from, to, request.getCategoryId());

        BigDecimal totalSpend = gastos.stream().map(Gasto::getMontoGasto).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgSpend = gastos.isEmpty() ? BigDecimal.ZERO : totalSpend.divide(BigDecimal.valueOf(gastos.size()), 2, RoundingMode.HALF_UP);

        String status = gastos.size() >= 10 ? "OK" : "INSUFFICIENT_DATA";
        Map<String, Object> facts = new LinkedHashMap<>();
        facts.put("period", Map.of("from", from, "to", to));
        facts.put("sampleSize", gastos.size());
        facts.put("totalSpend", totalSpend.setScale(2, RoundingMode.HALF_UP));
        facts.put("averageSpend", avgSpend);

        if (gastos.size() >= 10) {
            ForecastService.ForecastResult forecast = forecastService.forecastSpend(gastos, from, to, horizonDays);
            facts.put("forecast", Map.of(
                    "horizonDays", horizonDays,
                    "expectedSpend", forecast.expected(),
                    "lower95", forecast.lower95(),
                    "upper95", forecast.upper95()
            ));
        }

        String systemPrompt = "Eres un asistente financiero de ECONOMIX. Usa SOLO facts proporcionados. " +
                "No inventes datos. Si falta evidencia, dilo explícitamente y explica incertidumbre.";
        String userPrompt = "Pregunta del usuario: " + request.getMessage() + "\nFacts JSON: " + facts;

        String providerReply = geminiClient.generate(systemPrompt, userPrompt);
        if (providerReply == null || providerReply.isBlank()) {
            return AiChatResponse.builder()
                    .status("PROVIDER_UNAVAILABLE")
                    .facts(facts)
                    .reply(buildFallbackResponse(status, facts))
                    .build();
        }

        return AiChatResponse.builder()
                .status(status)
                .facts(facts)
                .reply(providerReply)
                .build();
    }

    private String buildFallbackResponse(String status, Map<String, Object> facts) {
        if ("INSUFFICIENT_DATA".equals(status)) {
            return "No hay datos suficientes para una recomendación estadística robusta. Registra al menos 10 gastos en el periodo.";
        }
        return "Proveedor de IA no disponible. Con los hechos actuales, prioriza controlar el gasto promedio y revisar la proyección incluida.";
    }
}
