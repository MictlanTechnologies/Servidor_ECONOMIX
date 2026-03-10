package sql.auth.exception;

public class AuthExceptions {

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) { super(message); }
    }

    public static class InvalidOtpException extends RuntimeException {
        public InvalidOtpException(String message) { super(message); }
    }

    public static class ChallengeExpiredException extends RuntimeException {
        public ChallengeExpiredException(String message) { super(message); }
    }

    public static class TooManyAttemptsException extends RuntimeException {
        public TooManyAttemptsException(String message) { super(message); }
    }

    public static class UnauthorizedAuthException extends RuntimeException {
        public UnauthorizedAuthException(String message) { super(message); }
    }

    public static class BadRequestAuthException extends RuntimeException {
        public BadRequestAuthException(String message) { super(message); }
    }
}
