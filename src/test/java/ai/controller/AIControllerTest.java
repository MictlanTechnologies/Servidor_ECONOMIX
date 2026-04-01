package ai.controller;

import ai.dto.AiChatResponse;
import ai.service.AiChatService;
import ai.service.AIDataService;
import ai.service.ForecastService;
import ai.service.InferentialStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sql.auth.security.CurrentUserService;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AIController.class)
@AutoConfigureMockMvc(addFilters = false)
class AIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private AIDataService aiDataService;
    @MockBean private ForecastService forecastService;
    @MockBean private InferentialStatsService inferentialStatsService;
    @MockBean private AiChatService aiChatService;
    @MockBean private CurrentUserService currentUserService;

    @Test
    void summaryShouldReturnInsufficientData() throws Exception {
        when(currentUserService.getRequiredUserId()).thenReturn(1);
        when(aiDataService.getGastos(eq(1), any(), any(), eq(null))).thenReturn(List.of());

        mockMvc.perform(get("/economix/api/ai/summary")
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INSUFFICIENT_DATA"));
    }

    @Test
    void chatShouldReturnProviderUnavailablePayload() throws Exception {
        when(currentUserService.getRequiredUserId()).thenReturn(1);
        when(aiChatService.chat(eq(1), any())).thenReturn(AiChatResponse.builder()
                .status("PROVIDER_UNAVAILABLE")
                .reply("fallback")
                .facts(Map.of("sampleSize", 0))
                .build());

        mockMvc.perform(post("/economix/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("message", "hola"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROVIDER_UNAVAILABLE"));
    }
}
