package lxxv.shared.javascript.util;

/**
 * Tracks simple resource usage snapshots.
 */
public class ResourceUsage {
    public static long usedHeapBytes() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public static long maxHeapBytes() {
        return Runtime.getRuntime().maxMemory();
    }
}
