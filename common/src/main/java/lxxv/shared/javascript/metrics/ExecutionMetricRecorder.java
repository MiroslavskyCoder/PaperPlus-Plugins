package lxxv.shared.javascript.metrics;

import lxxv.shared.javascript.runtime.ExecutionResult;

/**
 * Records per-execution metrics.
 */
public class ExecutionMetricRecorder {
    private final RuntimeMetrics runtimeMetrics;

    public ExecutionMetricRecorder(RuntimeMetrics runtimeMetrics) {
        this.runtimeMetrics = runtimeMetrics;
    }

    public void record(ExecutionResult result) {
        if (result == null) {
            return;
        }
        if (result.isSuccess()) {
            runtimeMetrics.markExecuted();
        } else {
            runtimeMetrics.markFailed();
        }
    }
}
