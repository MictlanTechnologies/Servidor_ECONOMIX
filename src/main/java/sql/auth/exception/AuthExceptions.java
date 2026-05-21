package sql.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AuthExceptions {

    private AuthExceptions() {
    }

    public static class UnauthorizedAuthException extends ResponseStatusException {
        public UnauthorizedAuthException(String reason) {
            super(HttpStatus.UNAUTHORIZED, reason);
        }
    }

    public static class BadRequestAuthException extends ResponseStatusException {
        public BadRequestAuthException(String reason) {
            super(HttpStatus.BAD_REQUEST, reason);
        }
    }

    public static class InvalidOtpException extends ResponseStatusException {
        public InvalidOtpException(String reason) {
            super(HttpStatus.UNAUTHORIZED, reason);
        }
    }
}
