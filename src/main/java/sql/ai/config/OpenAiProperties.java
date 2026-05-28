package sql.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
    private String apiKey;
    private String model = "gpt-4.1-mini";
    private Integer maxOutputTokens = 700;
    private Integer timeoutSeconds = 20;
}
