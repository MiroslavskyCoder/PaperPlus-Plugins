package lxxv.shared.javascript.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks basic runtime counters for JS execution.
 */
public class RuntimeMetrics {
    private final AtomicLong executedScripts = new AtomicLong();
    private final AtomicLong failedScripts = new AtomicLong();
    private final AtomicLong transpiledScripts = new AtomicLong();

    public void markExecuted() {
        executedScripts.incrementAndGet();
    }

    public void markFailed() {
        failedScripts.incrementAndGet();
    }

    public void markTranspiled() {
        transpiledScripts.incrementAndGet();
    }

    public long executed() {
        return executedScripts.get();
    }

    public long failed() {
        return failedScripts.get();
    }

    public long transpiled() {
        return transpiledScripts.get();
    }
}
