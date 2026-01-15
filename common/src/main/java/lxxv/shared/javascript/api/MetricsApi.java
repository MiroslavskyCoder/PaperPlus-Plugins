package lxxv.shared.javascript.api;

import java.util.Map;

import lxxv.shared.javascript.metrics.RuntimeMetrics;
import lxxv.shared.javascript.metrics.SystemMetrics;
import lxxv.shared.javascript.heap.HeapManager;

/**
 * Exposes metrics to JS consumers.
 */
public class MetricsApi {
    private final RuntimeMetrics runtimeMetrics;
    private final SystemMetrics systemMetrics;
    private final HeapManager heapManager;

    public MetricsApi(RuntimeMetrics runtimeMetrics, SystemMetrics systemMetrics, HeapManager heapManager) {
        this.runtimeMetrics = runtimeMetrics;
        this.systemMetrics = systemMetrics;
        this.heapManager = heapManager;
    }

    public Map<String, Object> runtime() {
        return Map.of(
                "executed", runtimeMetrics.executed(),
                "failed", runtimeMetrics.failed(),
                "transpiled", runtimeMetrics.transpiled()
        );
    }

    public Map<String, Object> system() {
        return Map.of(
                "cpuLoad", systemMetrics.cpuLoad().value(),
                "loadAvg", systemMetrics.systemLoadAvg().value(),
                "usedHeap", systemMetrics.usedMemoryBytes().value(),
                "heapBudgetUsed", heapManager == null ? null : heapManager.stats().usedBytes(),
                "heapBudgetMax", heapManager == null ? null : heapManager.stats().maxBytes()
        );
    }
}
