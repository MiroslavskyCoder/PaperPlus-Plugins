package lxxv.shared.javascript.sandbox;

/**
 * Defines sandbox rules for JS execution.
 */
public class SandboxPolicy {
    private final boolean allowIO;
    private final boolean allowNetwork;
    private final long maxCpuMillis;
    private final long maxHeapBytes;

    public SandboxPolicy(boolean allowIO, boolean allowNetwork, long maxCpuMillis, long maxHeapBytes) {
        this.allowIO = allowIO;
        this.allowNetwork = allowNetwork;
        this.maxCpuMillis = maxCpuMillis;
        this.maxHeapBytes = maxHeapBytes;
    }

    public boolean allowIO() {
        return allowIO;
    }

    public boolean allowNetwork() {
        return allowNetwork;
    }

    public long maxCpuMillis() {
        return maxCpuMillis;
    }

    public long maxHeapBytes() {
        return maxHeapBytes;
    }

    public static SandboxPolicy strictDefaults() {
        return new SandboxPolicy(false, false, 3_000, 64 * 1024 * 1024);
    }
}
