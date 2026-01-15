package lxxv.shared.javascript.heap;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility helpers for HeapBuffer operations.
 */
public final class HeapUtils {
    private HeapUtils() {}

    public static HeapBuffer concat(List<HeapBuffer> buffers) {
        if (buffers == null || buffers.isEmpty()) {
            return new HeapBuffer(0);
        }
        int total = buffers.stream().mapToInt(HeapBuffer::length).sum();
        HeapBuffer out = new HeapBuffer(total);
        int offset = 0;
        for (HeapBuffer buf : buffers) {
            if (buf == null) continue;
            buf.copyTo(out, offset);
            offset += buf.length();
        }
        return out;
    }

    public static HeapBuffer concat(HeapBuffer... buffers) {
        List<HeapBuffer> list = new ArrayList<>();
        if (buffers != null) {
            for (HeapBuffer b : buffers) {
                if (b != null) list.add(b);
            }
        }
        return concat(list);
    }

    public static HeapBuffer fromString(String value, String encoding) {
        if (value == null) {
            return new HeapBuffer(0);
        }
        byte[] bytes;
        if (encoding == null || encoding.equalsIgnoreCase("utf8") || encoding.equalsIgnoreCase("utf-8")) {
            bytes = value.getBytes(StandardCharsets.UTF_8);
        } else if (encoding.equalsIgnoreCase("ascii")) {
            bytes = value.getBytes(StandardCharsets.US_ASCII);
        } else if (encoding.equalsIgnoreCase("latin1") || encoding.equalsIgnoreCase("binary")) {
            bytes = value.getBytes(StandardCharsets.ISO_8859_1);
        } else {
            throw new IllegalArgumentException("Unsupported encoding: " + encoding);
        }
        return new HeapBuffer(bytes);
    }
}
