package sql.ai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
    @Value("${openai.api.key}")
    private String openAiApiKey;
    private String model = "gpt-5.5";
    private Integer maxOutputTokens = 700;
    private Integer timeoutSeconds = 20;
}
