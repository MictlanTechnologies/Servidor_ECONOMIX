package sql.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sql.auth.exception.AuthExceptions.BadRequestAuthException;
import sql.auth.exception.AuthExceptions.ChallengeExpiredException;
import sql.auth.exception.AuthExceptions.InvalidCredentialsException;
import sql.auth.exception.AuthExceptions.InvalidOtpException;
import sql.auth.exception.AuthExceptions.TooManyAttemptsException;
import sql.auth.exception.AuthExceptions.UnauthorizedAuthException;

import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler({InvalidCredentialsException.class, UnauthorizedAuthException.class})
    public ResponseEntity<Map<String, String>> handleUnauthorized(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({InvalidOtpException.class, BadRequestAuthException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ChallengeExpiredException.class)
    public ResponseEntity<Map<String, String>> handleGone(ChallengeExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TooManyAttemptsException.class)
    public ResponseEntity<Map<String, String>> handleTooMany(TooManyAttemptsException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno inesperado"));
    }
}
