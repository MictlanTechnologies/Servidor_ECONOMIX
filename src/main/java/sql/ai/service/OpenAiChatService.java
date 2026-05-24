package sql.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sql.ai.config.OpenAiProperties;
import sql.ai.prompt.EconomixAiPrompt;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiChatService {
    private final OpenAiProperties props;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate rt = new RestTemplate();

    public Map<String, Object> ask(String input) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(props.getOpenAiApiKey());
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = new HashMap<>();
            body.put("model", props.getModel());
            body.put("instructions", EconomixAiPrompt.SYSTEM_PROMPT);
            body.put("input", input);
            body.put("max_output_tokens", props.getMaxOutputTokens());
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> resp = rt.postForEntity("https://api.openai.com/v1/responses", entity, String.class);
            JsonNode root = mapper.readTree(resp.getBody());
            String text = root.path("output_text").asText("");
            return mapper.readValue(text, Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }
}
