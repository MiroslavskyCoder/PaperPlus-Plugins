package lxxv.shared.javascript.api;

import lxxv.shared.javascript.heap.HeapBuffer;
import lxxv.shared.javascript.heap.HeapManager;
import lxxv.shared.javascript.heap.HeapStats;

/**
 * JS-facing API for heap buffers similar to Node.js Buffer.
 */
public class HeapApi {
    private final HeapManager manager;

    public HeapApi(HeapManager manager) {
        this.manager = manager;
    }

    public HeapBuffer alloc(int size) {
        return manager.allocate(size);
    }

    public HeapBuffer allocUnsafe(int size) {
        // mirrors Node.js Buffer.allocUnsafe semantics
        return manager.allocate(size);
    }

    public HeapBuffer fromString(String value) {
        return manager.wrap(value == null ? new byte[0] : value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public void free(HeapBuffer buffer) {
        manager.release(buffer);
    }

    public HeapStats stats() {
        return manager.stats();
    }

    public void clear() {
        manager.clear();
    }
}
