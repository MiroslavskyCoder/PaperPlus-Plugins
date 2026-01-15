package lxxv.shared.javascript.runtime;

/**
 * Wraps execution output and diagnostics.
 */
public class ExecutionResult {
    private final Object value;
    private final String error;
    private final long durationMs;

    private ExecutionResult(Object value, String error, long durationMs) {
        this.value = value;
        this.error = error;
        this.durationMs = durationMs;
    }

    public static ExecutionResult success(Object value, long durationMs) {
        return new ExecutionResult(value, null, durationMs);
    }

    public static ExecutionResult failure(String error, long durationMs) {
        return new ExecutionResult(null, error, durationMs);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public Object value() {
        return value;
    }

    public String error() {
        return error;
    }

    public long durationMs() {
        return durationMs;
    }
}
