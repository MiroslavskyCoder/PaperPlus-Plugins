package lxxv.shared.javascript.mono;

/**
 * Monotonic clock utility (nanosecond resolution).
 */
public final class MonoClock {
    private MonoClock() {}

    public static long nowNanos() {
        return System.nanoTime();
    }

    public static long nowMillis() {
        return System.nanoTime() / 1_000_000;
    }
}
