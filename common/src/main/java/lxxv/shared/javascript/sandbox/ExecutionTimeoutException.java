package lxxv.shared.javascript.sandbox;

/**
 * Raised when a script exceeds its allotted execution time.
 */
public class ExecutionTimeoutException extends RuntimeException {
    public ExecutionTimeoutException(String message) {
        super(message);
    }

    public ExecutionTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
