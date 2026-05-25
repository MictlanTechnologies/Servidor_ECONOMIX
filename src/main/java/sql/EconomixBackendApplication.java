package sql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
//YA JALA CUATEEEEE
@SpringBootApplication
@ConfigurationPropertiesScan
public class EconomixBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(EconomixBackendApplication.class, args);
    }
}
