package lxxv.shared.javascript.sandbox;

import lxxv.shared.javascript.runtime.ExecutionOptions;

/**
 * Performs lightweight sandbox validation before execution.
 */
public class SandboxGuard {
    public void validate(ExecutionOptions options, SandboxPolicy policy) {
        if (options == null || policy == null) {
            return;
        }
        if (!policy.allowIO() && options.allowIO()) {
            throw new IllegalStateException("IO not permitted by sandbox policy");
        }
        if (!policy.allowNetwork() && options.allowNetwork()) {
            throw new IllegalStateException("Network not permitted by sandbox policy");
        }
        if (options.maxMemoryBytes() > policy.maxHeapBytes()) {
            throw new IllegalStateException("Requested heap exceeds sandbox policy");
        }
    }
}
