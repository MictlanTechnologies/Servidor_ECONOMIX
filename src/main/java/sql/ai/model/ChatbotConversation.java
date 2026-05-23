package sql.ai.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_chatbot_conversation")
@Data
public class ChatbotConversation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer idUsuario;
    @Column(length = 1500)
    private String mensajeUsuario;
    @Column(length = 4000)
    private String respuestaIa;
    private LocalDateTime fechaHora;
    @Column(length = 2000)
    private String resumenDatosUsado;
    @Column(length = 20)
    private String nivelRiesgoFinanciero;
}
