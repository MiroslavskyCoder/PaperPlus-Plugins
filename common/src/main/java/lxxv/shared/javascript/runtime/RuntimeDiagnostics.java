package lxxv.shared.javascript.runtime;

import java.util.Map;

/**
 * Simple holder for diagnostics data after execution.
 */
public class RuntimeDiagnostics {
    private final Diagnostics diagnostics;
    private final ExecutionResult result;
    private final Map<String, Object> snapshot;

    public RuntimeDiagnostics(Diagnostics diagnostics, ExecutionResult result, Map<String, Object> snapshot) {
        this.diagnostics = diagnostics;
        this.result = result;
        this.snapshot = snapshot;
    }

    public Diagnostics diagnostics() {
        return diagnostics;
    }

    public ExecutionResult result() {
        return result;
    }

    public Map<String, Object> snapshot() {
        return snapshot;
    }
}
