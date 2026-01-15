package lxxv.shared.javascript.api;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Map;

import lxxv.shared.javascript.api.HeapApi;
import lxxv.shared.javascript.heap.HeapStats;
import lxxv.shared.javascript.util.ResourceUsage;

/**
 * Minimal process-like API inspired by Node.js: memoryUsage and uptime/hrtime.
 */
public class ProcessApi {
    private final long startNanos;
    private final HeapApi heapApi;

    public ProcessApi(HeapApi heapApi) {
        this.startNanos = System.nanoTime();
        this.heapApi = heapApi;
    }

    public Map<String, Object> memoryUsage() {
        HeapStats heapStats = heapApi.stats();
        return Map.of(
                "rss", ResourceUsage.maxHeapBytes(),
                "heapTotal", heapStats.maxBytes(),
                "heapUsed", heapStats.usedBytes(),
                "external", 0L
        );
    }

    public long uptimeSeconds() {
        return Duration.ofNanos(System.nanoTime() - startNanos).toSeconds();
    }

    public long[] hrtime() {
        long nanos = System.nanoTime() - startNanos;
        long seconds = nanos / 1_000_000_000L;
        long nanosec = nanos % 1_000_000_000L;
        return new long[] { seconds, nanosec };
    }

    public long[] hrtime(long[] previous) {
        long nanos = System.nanoTime() - startNanos;
        long seconds = nanos / 1_000_000_000L;
        long nanosec = nanos % 1_000_000_000L;
        if (previous == null || previous.length < 2) {
            return new long[] { seconds, nanosec };
        }
        long prevNanos = previous[0] * 1_000_000_000L + previous[1];
        long delta = nanos - prevNanos;
        return new long[] { delta / 1_000_000_000L, delta % 1_000_000_000L };
    }

    public int pid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        try {
            return Integer.parseInt(name.split("@")[0]);
        } catch (Exception e) {
            return -1;
        }
    }
}
