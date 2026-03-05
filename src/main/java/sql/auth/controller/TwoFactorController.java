package sql.auth.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sql.auth.dto.AuthDtos;
import sql.auth.service.TokenService;
import sql.auth.service.TwoFactorService;

@RestController
@RequestMapping("/users/2fa")
public class TwoFactorController {

    private final TwoFactorService twoFactorService;
    private final TokenService tokenService;

    public TwoFactorController(TwoFactorService twoFactorService, TokenService tokenService) {
        this.twoFactorService = twoFactorService;
        this.tokenService = tokenService;
    }

    @PostMapping("/setup")
    public ResponseEntity<AuthDtos.TwoFactorSetupResponse> setup(HttpServletRequest request) {
        Integer userId = extractUserId(request);
        return ResponseEntity.ok(twoFactorService.setup(userId));
    }

    @PostMapping("/enable")
    public ResponseEntity<AuthDtos.TwoFactorToggleResponse> enable(@RequestBody AuthDtos.TwoFactorOtpRequest request,
                                                                    HttpServletRequest httpRequest) {
        Integer userId = extractUserId(httpRequest);
        return ResponseEntity.ok(twoFactorService.enable(userId, request.getOtpCode()));
    }

    @PostMapping("/disable")
    public ResponseEntity<AuthDtos.TwoFactorToggleResponse> disable(@RequestBody AuthDtos.TwoFactorOtpRequest request,
                                                                     HttpServletRequest httpRequest) {
        Integer userId = extractUserId(httpRequest);
        return ResponseEntity.ok(twoFactorService.disable(userId, request.getOtpCode()));
    }

    private Integer extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization Bearer token requerido");
        }
        String token = authHeader.substring(7);
        Claims claims = tokenService.parseAccessToken(token);
        Number userId = (Number) claims.get("userId");
        return userId.intValue();
    }
}
