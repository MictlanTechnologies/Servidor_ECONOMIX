package sql.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sql.auth.dto.AuthDtos;
import sql.auth.exception.AuthExceptions.BadRequestAuthException;
import sql.auth.security.CurrentUserService;
import sql.auth.service.TwoFactorService;

@RestController
@RequestMapping("/users/2fa")
public class TwoFactorController {

    private final TwoFactorService twoFactorService;
    private final CurrentUserService currentUserService;

    public TwoFactorController(TwoFactorService twoFactorService, CurrentUserService currentUserService) {
        this.twoFactorService = twoFactorService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/setup")
    public ResponseEntity<AuthDtos.TwoFactorSetupResponse> setup() {
        return ResponseEntity.ok(twoFactorService.setup(currentUserService.getRequiredUserId()));
    }

    @PostMapping("/enable")
    public ResponseEntity<AuthDtos.TwoFactorToggleResponse> enable(@RequestBody AuthDtos.TwoFactorOtpRequest request) {
        if (request == null || request.getOtpCode() == null || request.getOtpCode().isBlank()) {
            throw new BadRequestAuthException("otpCode es obligatorio");
        }
        return ResponseEntity.ok(twoFactorService.enable(currentUserService.getRequiredUserId(), request.getOtpCode()));
    }

    @PostMapping("/disable")
    public ResponseEntity<AuthDtos.TwoFactorToggleResponse> disable(@RequestBody AuthDtos.TwoFactorOtpRequest request) {
        if (request == null || request.getOtpCode() == null || request.getOtpCode().isBlank()) {
            throw new BadRequestAuthException("otpCode es obligatorio");
        }
        return ResponseEntity.ok(twoFactorService.disable(currentUserService.getRequiredUserId(), request.getOtpCode()));
    }
}
