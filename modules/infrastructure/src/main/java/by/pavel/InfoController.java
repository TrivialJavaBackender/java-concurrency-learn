package by.pavel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InfoController {

    @Value("${spring.application.name:infra-learn}")
    private String appName;

    @Value("${APP_ENV:local}")
    private String env;

    @GetMapping("/api/info")
    public Map<String, String> info() {
        return Map.of(
            "app", appName,
            "env", env,
            "version", "1.0.0"
        );
    }
}
