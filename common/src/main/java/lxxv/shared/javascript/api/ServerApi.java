package lxxv.shared.javascript.api;

import java.time.Instant;
import java.util.Map;

import lxxv.shared.javascript.metrics.RuntimeMetrics;
import lxxv.shared.javascript.util.AsyncPool;
import lxxv.shared.javascript.util.IoLoop;

/**
 * Lightweight facade exposing server-side helpers to JS scripts.
 */
public class ServerApi {
    private final RuntimeMetrics runtimeMetrics;
    private final AsyncPool asyncPool;
    private final IoLoop ioLoop;

    public ServerApi(RuntimeMetrics runtimeMetrics, AsyncPool asyncPool, IoLoop ioLoop) {
        this.runtimeMetrics = runtimeMetrics;
        this.asyncPool = asyncPool;
        this.ioLoop = ioLoop;
    }

    public Map<String, Long> stats() {
        return Map.of(
                "executed", runtimeMetrics.executed(),
                "failed", runtimeMetrics.failed(),
                "transpiled", runtimeMetrics.transpiled()
        );
    }

    public void schedule(Runnable task, long delayMs) {
        ioLoop.schedule(task, delayMs);
    }

    public void scheduleRepeating(Runnable task, long initialDelayMs, long periodMs) {
        ioLoop.scheduleAtFixedRate(task, initialDelayMs, periodMs);
    }

    public void submitAsync(Runnable task) {
        asyncPool.submit(task);
    }

    public long nowMillis() {
        return Instant.now().toEpochMilli();
    }
}
