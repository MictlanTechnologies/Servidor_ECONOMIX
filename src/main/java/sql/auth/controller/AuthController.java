package sql.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sql.auth.dto.AuthDtos;
import sql.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@RequestBody AuthDtos.LoginRequest request,
                                                       HttpServletRequest httpRequest) {
        String device = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.login(request, ip, device));
    }

    @PostMapping("/2fa/verify")
    public ResponseEntity<AuthDtos.AuthResponse> verify2fa(@RequestBody AuthDtos.TwoFactorVerifyRequest request,
                                                            HttpServletRequest httpRequest) {
        String device = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.verifyTwoFactor(request, ip, device));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDtos.RefreshResponse> refresh(@RequestBody AuthDtos.RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody AuthDtos.LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
