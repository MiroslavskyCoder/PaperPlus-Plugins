package lxxv.shared.javascript.runtime;

/**
 * Execution-time options to control sandbox behavior.
 */
public class ExecutionOptions {
    private final long timeoutMs;
    private final boolean allowIO;
    private final boolean allowNetwork;
    private final long maxMemoryBytes;

    public ExecutionOptions(long timeoutMs, boolean allowIO, boolean allowNetwork, long maxMemoryBytes) {
        this.timeoutMs = timeoutMs;
        this.allowIO = allowIO;
        this.allowNetwork = allowNetwork;
        this.maxMemoryBytes = maxMemoryBytes;
    }

    public long timeoutMs() {
        return timeoutMs;
    }

    public boolean allowIO() {
        return allowIO;
    }

    public boolean allowNetwork() {
        return allowNetwork;
    }

    public long maxMemoryBytes() {
        return maxMemoryBytes;
    }

    public static ExecutionOptions defaults() {
        return new ExecutionOptions(5_000, false, false, 64 * 1024 * 1024);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private long timeoutMs = 5_000;
        private boolean allowIO = false;
        private boolean allowNetwork = false;
        private long maxMemoryBytes = 64 * 1024 * 1024;

        public Builder timeoutMs(long value) {
            this.timeoutMs = value;
            return this;
        }

        public Builder allowIO(boolean value) {
            this.allowIO = value;
            return this;
        }

        public Builder allowNetwork(boolean value) {
            this.allowNetwork = value;
            return this;
        }

        public Builder maxMemoryBytes(long value) {
            this.maxMemoryBytes = value;
            return this;
        }

        public ExecutionOptions build() {
            return new ExecutionOptions(timeoutMs, allowIO, allowNetwork, maxMemoryBytes);
        }
    }
}
