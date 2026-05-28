package sql.ai.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatbotMessageRequestValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void mensajeNormalDebePasarValidacion() {
        ChatbotMessageRequest req = new ChatbotMessageRequest();
        req.setIdUsuario(1);
        req.setMensaje("Analiza mis gastos");

        Set<?> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void mensajeVacioDebeFallar() {
        ChatbotMessageRequest req = new ChatbotMessageRequest();
        req.setIdUsuario(1);
        req.setMensaje("   ");

        var violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.toString().contains("mensaje")));
    }

    @Test
    void mensajeMayorA1200DebeFallar() {
        ChatbotMessageRequest req = new ChatbotMessageRequest();
        req.setIdUsuario(1);
        req.setMensaje("a".repeat(1201));

        var violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.toString().contains("1200")));
    }
}
