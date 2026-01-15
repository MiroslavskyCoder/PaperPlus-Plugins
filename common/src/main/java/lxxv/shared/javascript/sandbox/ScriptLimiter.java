package lxxv.shared.javascript.sandbox;

/**
 * Tracks execution duration to enforce timeouts.
 */
public class ScriptLimiter {
    private final long startedAt = System.nanoTime();

    public boolean exceeded(long timeoutMs) {
        if (timeoutMs <= 0) {
            return false;
        }
        long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;
        return elapsedMs > timeoutMs;
    }
}
