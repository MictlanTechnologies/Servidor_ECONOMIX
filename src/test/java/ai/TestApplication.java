package ai;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Minimal Spring Boot configuration used by tests in the ai package.
 *
 * <p>The production application class lives in the {@code sql} package, so
 * Spring Boot's test bootstrapper cannot discover it from tests located under
 * {@code ai.*}. Providing this test-scoped configuration lets slice tests such
 * as {@code @WebMvcTest} start without changing the production package layout.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class TestApplication {
}
