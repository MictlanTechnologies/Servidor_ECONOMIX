package sql.ai.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sql.ai.dto.ChatbotMessageRequest;
import sql.ai.dto.ChatbotMessageResponse;
import sql.ai.service.ChatbotService;

@RestController
@RequestMapping("/economix/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatbotMessageResponse> message(@Valid @RequestBody ChatbotMessageRequest request) {
        // Integrar aquí validación contra usuario autenticado JWT: id autenticado debe coincidir con request.idUsuario.
        return ResponseEntity.ok(chatbotService.process(request));
    }
}
