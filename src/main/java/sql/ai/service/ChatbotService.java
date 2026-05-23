package sql.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sql.ai.analytics.FinancialStatisticsService;
import sql.ai.dto.*;
import sql.model.Gasto;
import sql.model.Ingreso;
import sql.repository.GastoRepository;
import sql.repository.IngresoRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatbotService {
    private final FinancialContextService contextService;
    private final FinancialStatisticsService statsService;
    private final OpenAiChatService openAiChatService;
    private final GastoRepository gastoRepository;
    private final IngresoRepository ingresoRepository;

    public ChatbotMessageResponse process(ChatbotMessageRequest req) {
        String msg = sanitize(req.getMensaje());
        if (msg.isBlank() || msg.length() > 1200 || fueraDeTema(msg)) throw new IllegalArgumentException("Mensaje inválido o fuera de tema");

        FinancialContextSummary summary = contextService.buildSummary(req.getIdUsuario());
        List<BigDecimal> gastos = gastoRepository.findByIdUsuario(req.getIdUsuario()).stream().map(Gasto::getMontoGasto).toList();
        List<BigDecimal> ingresos = ingresoRepository.findByIdUsuario(req.getIdUsuario()).stream().map(Ingreso::getMontoIngreso).toList();
        StatisticalAnalysisResult stats = statsService.analyze(gastos, ingresos, summary.getGastoTotalPeriodo(), summary.getIngresoTotalPeriodo());

        String input = "Usuario: " + msg + "\nResumen: " + summary + "\nEstadísticas: " + stats +
                "\nDevuelve JSON con respuesta,recomendaciones,alertas,nivelRiesgoFinanciero,datosInsuficientes,accionesSugeridas,disclaimer";
        Map<String, Object> ai = openAiChatService.ask(input);

        if (ai.isEmpty()) {
            return ChatbotMessageResponse.builder().respuesta("No pude generar una respuesta con IA en este momento, pero con tus datos actuales puedo decir que tu balance neto es " + summary.getBalanceNeto())
                    .resumenFinancieroUsado(Map.of("balanceNeto", summary.getBalanceNeto(), "gastoTotal", summary.getGastoTotalPeriodo()))
                    .recomendaciones(List.of("Registra más gastos por categoría para mejorar el análisis.")).alertas(summary.getPresupuestosExcedidos())
                    .nivelRiesgoFinanciero(stats.getNivelRiesgoFinanciero()).datosInsuficientes(summary.isDatosInsuficientes() || stats.isDatosInsuficientes())
                    .accionesSugeridas(List.of("Define presupuestos mensuales por categoría."))
                    .disclaimer("Orientación educativa, no asesoría financiera profesional.").build();
        }
        return ChatbotMessageResponse.builder().respuesta((String) ai.getOrDefault("respuesta", "Sin respuesta"))
                .resumenFinancieroUsado(Map.of("ingresoTotalPeriodo", summary.getIngresoTotalPeriodo(), "gastoTotalPeriodo", summary.getGastoTotalPeriodo(), "balanceNeto", summary.getBalanceNeto()))
                .recomendaciones((List<String>) ai.getOrDefault("recomendaciones", List.of()))
                .alertas((List<String>) ai.getOrDefault("alertas", List.of()))
                .nivelRiesgoFinanciero((String) ai.getOrDefault("nivelRiesgoFinanciero", stats.getNivelRiesgoFinanciero()))
                .datosInsuficientes((boolean) ai.getOrDefault("datosInsuficientes", summary.isDatosInsuficientes() || stats.isDatosInsuficientes()))
                .accionesSugeridas((List<String>) ai.getOrDefault("accionesSugeridas", List.of()))
                .disclaimer((String) ai.getOrDefault("disclaimer", "Orientación educativa, no asesoría financiera profesional."))
                .build();
    }

    private String sanitize(String message) { return message == null ? "" : message.replaceAll("[\\n\\r\\t]+", " ").trim(); }
    private boolean fueraDeTema(String m) { return m.toLowerCase().contains("cripto") || m.toLowerCase().contains("apuesta"); }
}
