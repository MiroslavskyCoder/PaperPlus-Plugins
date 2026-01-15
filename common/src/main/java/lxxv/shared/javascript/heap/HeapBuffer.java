package lxxv.shared.javascript.heap;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Lightweight byte buffer similar to Node.js Buffer semantics.
 */
public class HeapBuffer {
    private final byte[] data;

    HeapBuffer(int size) {
        this.data = new byte[size];
    }

    HeapBuffer(byte[] seed) {
        this.data = seed;
    }

    public int length() {
        return data.length;
    }

    public byte get(int index) {
        checkIndex(index);
        return data[index];
    }

    public void set(int index, byte value) {
        checkIndex(index);
        data[index] = value;
    }

    public void fill(byte value) {
        Arrays.fill(data, value);
    }

    public void copyFrom(byte[] src, int offset) {
        if (src == null) return;
        if (offset < 0 || offset + src.length > data.length) {
            throw new IndexOutOfBoundsException("Copy exceeds buffer bounds");
        }
        System.arraycopy(src, 0, data, offset, src.length);
    }

    public byte[] toByteArray() {
        return data;
    }

    public String toUtf8String() {
        return new String(data, StandardCharsets.UTF_8);
    }

    public void writeUtf8(String value) {
        if (value == null) return;
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        int len = Math.min(bytes.length, data.length);
        System.arraycopy(bytes, 0, data, 0, len);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= data.length) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + data.length);
        }
    }
}
