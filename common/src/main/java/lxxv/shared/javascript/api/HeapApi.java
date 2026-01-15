package lxxv.shared.javascript.api;

import lxxv.shared.javascript.heap.HeapBuffer;
import lxxv.shared.javascript.heap.HeapManager;
import lxxv.shared.javascript.heap.HeapStats;
import lxxv.shared.javascript.heap.HeapUtils;

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

    public HeapBuffer fromString(String value, String encoding) {
        return HeapUtils.fromString(value, encoding);
    }

    public HeapBuffer concat(HeapBuffer... buffers) {
        return HeapUtils.concat(buffers);
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
