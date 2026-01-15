package lxxv.shared.javascript.api;

import java.util.Map;

import lxxv.shared.javascript.metrics.RuntimeMetrics;
import lxxv.shared.javascript.metrics.SystemMetrics;

/**
 * Exposes metrics to JS consumers.
 */
public class MetricsApi {
    private final RuntimeMetrics runtimeMetrics;
    private final SystemMetrics systemMetrics;

    public MetricsApi(RuntimeMetrics runtimeMetrics, SystemMetrics systemMetrics) {
        this.runtimeMetrics = runtimeMetrics;
        this.systemMetrics = systemMetrics;
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
                "usedHeap", systemMetrics.usedMemoryBytes().value()
        );
    }
}
