package lxxv.shared.javascript.heap;

/**
 * Exposes heap usage statistics for JS-facing API.
 */
public class HeapStats {
    private final long usedBytes;
    private final long maxBytes;

    public HeapStats(long usedBytes, long maxBytes) {
        this.usedBytes = usedBytes;
        this.maxBytes = maxBytes;
    }

    public long usedBytes() {
        return usedBytes;
    }

    public long maxBytes() {
        return maxBytes;
    }
}
