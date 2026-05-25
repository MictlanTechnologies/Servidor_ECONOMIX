package sql.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import sql.ai.config.OpenAiProperties;
import sql.ai.prompt.EconomixAiPrompt;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiChatService {
    private static final Logger log = LoggerFactory.getLogger(OpenAiChatService.class);
    private static final String OPENAI_RESPONSES_URL = "https://api.openai.com/v1/responses";
    private static final String DEFAULT_MODEL = "gpt-4.1-mini";

    private final OpenAiProperties props;
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> ask(String input) {
        String apiKey = props.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.error("OpenAI no invocado: OPENAI_API_KEY no está configurada");
            throw new IllegalStateException("OPENAI_API_KEY no está configurada");
        }

        String model = resolveModel(props.getModel());
        RestTemplate rt = buildRestTemplate(props.getTimeoutSeconds());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("instructions", EconomixAiPrompt.SYSTEM_PROMPT + "\nDevuelve JSON puro, sin markdown ni texto adicional.");
            body.put("input", input);
            body.put("max_output_tokens", props.getMaxOutputTokens());
            body.put("text", Map.of("format", Map.of("type", "json_object")));

            log.info("Invocando OpenAI Responses API con modelo={}", model);
            ResponseEntity<String> resp = rt.postForEntity(OPENAI_RESPONSES_URL, new HttpEntity<>(body, headers), String.class);

            if (!resp.getStatusCode().is2xxSuccessful()) {
                log.error("OpenAI respondió status no exitoso: status={} body={}", resp.getStatusCode().value(), summarize(resp.getBody()));
                return Map.of();
            }

            return parseOpenAiResponse(resp.getBody());
        } catch (HttpStatusCodeException ex) {
            log.error("Error HTTP OpenAI: status={} body={} message={}", ex.getStatusCode().value(), summarize(ex.getResponseBodyAsString(StandardCharsets.UTF_8)), ex.getMessage());
            return Map.of();
        } catch (ResourceAccessException ex) {
            log.error("Timeout o error de conectividad con OpenAI: {}", ex.getMessage());
            return Map.of();
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al invocar OpenAI: tipo={} mensaje={}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
            return Map.of();
        }
    }

    Map<String, Object> parseOpenAiResponse(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            String rawText = extractOutputText(root);
            if (rawText == null || rawText.isBlank()) {
                log.warn("OpenAI devolvió output_text vacío y no se pudo extraer texto desde output");
                return Map.of();
            }

            String cleaned = normalizeJsonText(rawText);
            if (cleaned == null || cleaned.isBlank()) {
                log.warn("No se encontró un JSON parseable dentro del output de OpenAI: {}", summarize(rawText));
                return Map.of();
            }

            return mapper.readValue(cleaned, new TypeReference<>() {});
        } catch (Exception ex) {
            log.error("No se pudo parsear respuesta de OpenAI a JSON. tipo={} mensaje={} body={}", ex.getClass().getSimpleName(), ex.getMessage(), summarize(responseBody));
            return Map.of();
        }
    }

    String extractOutputText(JsonNode root) {
        String outputText = root.path("output_text").asText("");
        if (!outputText.isBlank()) return outputText;

        JsonNode output = root.path("output");
        if (output.isArray()) {
            for (JsonNode item : output) {
                JsonNode content = item.path("content");
                if (content.isArray()) {
                    for (JsonNode c : content) {
                        String text = c.path("text").asText("");
                        if (!text.isBlank()) return text;
                    }
                }
            }
        }
        return "";
    }

    String normalizeJsonText(String text) {
        String cleaned = text.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```(?:json)?\\s*", "").replaceAll("\\s*```$", "").trim();
        }
        int first = cleaned.indexOf('{');
        int last = cleaned.lastIndexOf('}');
        if (first >= 0 && last > first) {
            cleaned = cleaned.substring(first, last + 1);
        }
        return cleaned;
    }

    String resolveModel(String configured) {
        if (configured == null || configured.isBlank()) return DEFAULT_MODEL;
        return configured.trim();
    }

    RestTemplate buildRestTemplate(Integer timeoutSeconds) {
        int timeoutMs = Math.max(5, timeoutSeconds == null ? 20 : timeoutSeconds) * 1000;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        return new RestTemplate(factory);
    }

    private String summarize(String text) {
        if (text == null) return "null";
        String sanitized = text.replaceAll("[\\n\\r\\t]+", " ");
        return sanitized.length() > 300 ? sanitized.substring(0, 300) + "..." : sanitized;
    }
}
