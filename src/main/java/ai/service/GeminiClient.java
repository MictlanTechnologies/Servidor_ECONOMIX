package ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GeminiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiClient(RestClient.Builder builder,
                        ObjectMapper objectMapper,
                        @Value("${economix.ai.gemini.api-key:}") String apiKey,
                        @Value("${economix.ai.gemini.model:gemini-1.5-flash}") String model) {
        this.restClient = builder.baseUrl("https://generativelanguage.googleapis.com").build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public String generate(String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        try {
            Map<String, Object> request = Map.of(
                    "system_instruction", Map.of("parts", new Object[]{Map.of("text", systemPrompt)}),
                    "contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", userPrompt)})},
                    "generationConfig", Map.of("temperature", 0.2, "maxOutputTokens", 600)
            );

            String payload = restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/v1beta/models/{model}:generateContent").queryParam("key", apiKey).build(model))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(payload);
            JsonNode text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            return text.isMissingNode() ? null : text.asText();
        } catch (Exception ex) {
            return null;
        }
    }
}
