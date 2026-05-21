package sql.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users/2fa")
public class TwoFactorController {

    @PostMapping("/setup")
    public ResponseEntity<Map<String, String>> setup() {
        return ResponseEntity.ok(Map.of("secret", "disabled_in_legacy_branch"));
    }

    @PostMapping("/enable")
    public ResponseEntity<Map<String, Boolean>> enable() {
        return ResponseEntity.ok(Map.of("enabled", false));
    }

    @PostMapping("/disable")
    public ResponseEntity<Map<String, Boolean>> disable() {
        return ResponseEntity.ok(Map.of("enabled", false));
    }
}
