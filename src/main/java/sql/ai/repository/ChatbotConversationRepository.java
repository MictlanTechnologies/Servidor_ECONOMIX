package sql.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sql.ai.model.ChatbotConversation;

public interface ChatbotConversationRepository extends JpaRepository<ChatbotConversation, Long> {}
