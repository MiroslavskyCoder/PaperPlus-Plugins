package lxxv.shared.javascript.heap;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages heap buffers with a global byte budget, similar to Node.js Buffer allocations.
 */
public class HeapManager {
    private final long maxBytes;
    private final AtomicLong usedBytes = new AtomicLong(0);
    private final Map<String, HeapBuffer> buffers = new ConcurrentHashMap<>();

    public HeapManager(long maxBytes) {
        this.maxBytes = Math.max(1, maxBytes);
    }

    public HeapBuffer allocate(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be > 0");
        }
        long newTotal = usedBytes.addAndGet(size);
        if (newTotal > maxBytes) {
            usedBytes.addAndGet(-size);
            throw new IllegalStateException("Heap limit exceeded: " + newTotal + " > " + maxBytes);
        }
        HeapBuffer buffer = new HeapBuffer(size);
        buffers.put(UUID.randomUUID().toString(), buffer);
        return buffer;
    }

    public HeapBuffer wrap(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data must not be null");
        }
        long newTotal = usedBytes.addAndGet(data.length);
        if (newTotal > maxBytes) {
            usedBytes.addAndGet(-data.length);
            throw new IllegalStateException("Heap limit exceeded: " + newTotal + " > " + maxBytes);
        }
        HeapBuffer buffer = new HeapBuffer(data);
        buffers.put(UUID.randomUUID().toString(), buffer);
        return buffer;
    }

    public void release(HeapBuffer buffer) {
        if (buffer == null) return;
        buffers.values().removeIf(v -> v == buffer);
        usedBytes.addAndGet(-buffer.length());
        if (usedBytes.get() < 0) {
            usedBytes.set(0);
        }
    }

    public HeapStats stats() {
        return new HeapStats(usedBytes.get(), maxBytes);
    }

    public void clear() {
        buffers.clear();
        usedBytes.set(0);
    }
}
