package sql.auth.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sql.auth.dto.AuthDtos;
import sql.auth.exception.AuthExceptions.BadRequestAuthException;
import sql.auth.exception.AuthExceptions.UnauthorizedAuthException;
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
    public ResponseEntity<AuthDtos.TwoFactorToggleResponse> enable(HttpServletRequest request, @RequestBody AuthDtos.TwoFactorOtpRequest body) {
        if (body == null || body.getOtpCode() == null || body.getOtpCode().isBlank()) {
            throw new BadRequestAuthException("otpCode es obligatorio.");
        }
        Integer userId = extractUserId(request);
        return ResponseEntity.ok(twoFactorService.enable(userId, body.getOtpCode()));
    }

    @PostMapping("/disable")
    public ResponseEntity<AuthDtos.TwoFactorToggleResponse> disable(HttpServletRequest request, @RequestBody AuthDtos.TwoFactorOtpRequest body) {
        if (body == null || body.getOtpCode() == null || body.getOtpCode().isBlank()) {
            throw new BadRequestAuthException("otpCode es obligatorio.");
        }
        Integer userId = extractUserId(request);
        return ResponseEntity.ok(twoFactorService.disable(userId, body.getOtpCode()));
    }

    private Integer extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedAuthException("Authorization Bearer requerido.");
        }
        String token = authHeader.substring(7).trim();
        try {
            Claims claims = tokenService.parseAccessToken(token);
            Object userIdClaim = claims.get("userId");
            if (userIdClaim instanceof Integer integer) {
                return integer;
            }
            if (userIdClaim instanceof Number number) {
                return number.intValue();
            }
            return Integer.parseInt(String.valueOf(userIdClaim));
        } catch (JwtException e) {
            throw new UnauthorizedAuthException("Token inválido o expirado.");
        }
    }
}
