package sql.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import sql.ai.config.OpenAiProperties;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OpenAiChatServiceTest {

    @Test
    void modeloVacioUsaDefault() {
        OpenAiChatService service = new OpenAiChatService(new OpenAiProperties());
        assertEquals("gpt-4.1-mini", service.resolveModel(" "));
    }

    @Test
    void parseaOutputTextJsonValido() throws Exception {
        OpenAiChatService service = new OpenAiChatService(new OpenAiProperties());
        String body = new ObjectMapper().writeValueAsString(Map.of("output_text", "{\"respuesta\":\"ok\"}"));
        Map<String, Object> result = service.parseOpenAiResponse(body);
        assertEquals("ok", result.get("respuesta"));
    }

    @Test
    void parseaOutputTextConMarkdown() throws Exception {
        OpenAiChatService service = new OpenAiChatService(new OpenAiProperties());
        String body = new ObjectMapper().writeValueAsString(Map.of("output_text", "```json\n{\"respuesta\":\"ok\"}\n```"));
        Map<String, Object> result = service.parseOpenAiResponse(body);
        assertEquals("ok", result.get("respuesta"));
    }

    @Test
    void outputTextVacioDevuelveMapaVacio() throws Exception {
        OpenAiChatService service = new OpenAiChatService(new OpenAiProperties());
        String body = new ObjectMapper().writeValueAsString(Map.of("output_text", ""));
        Map<String, Object> result = service.parseOpenAiResponse(body);
        assertTrue(result.isEmpty());
    }

    @Test
    void sinApiKeyLanzaErrorControlado() {
        OpenAiProperties properties = new OpenAiProperties();
        properties.setApiKey("  ");
        OpenAiChatService service = new OpenAiChatService(properties);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.ask("hola"));
        assertTrue(ex.getMessage().contains("OPENAI_API_KEY"));
    }
}
