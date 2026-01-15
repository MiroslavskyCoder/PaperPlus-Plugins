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

    public boolean isEmpty() {
        return data.length == 0;
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

    public void fill(byte value, int start, int end) {
        if (start < 0 || end > data.length || start > end) {
            throw new IndexOutOfBoundsException("Invalid fill range");
        }
        Arrays.fill(data, start, end, value);
    }

    public void copyFrom(byte[] src, int offset) {
        if (src == null) return;
        if (offset < 0 || offset + src.length > data.length) {
            throw new IndexOutOfBoundsException("Copy exceeds buffer bounds");
        }
        System.arraycopy(src, 0, data, offset, src.length);
    }

    public HeapBuffer slice(int start, int end) {
        if (start < 0) start = 0;
        if (end > data.length) end = data.length;
        if (start > end) start = end;
        return new HeapBuffer(Arrays.copyOfRange(data, start, end));
    }

    public void copyTo(HeapBuffer target, int targetOffset) {
        if (target == null) return;
        if (targetOffset < 0 || targetOffset + data.length > target.length()) {
            throw new IndexOutOfBoundsException("Copy exceeds target bounds");
        }
        System.arraycopy(this.data, 0, target.data, targetOffset, this.data.length);
    }

    public byte[] toByteArray() {
        return data;
    }

    public String toUtf8String() {
        return new String(data, StandardCharsets.UTF_8);
    }

    public String toString(String encoding) {
        if (encoding == null || encoding.equalsIgnoreCase("utf8") || encoding.equalsIgnoreCase("utf-8")) {
            return toUtf8String();
        }
        if (encoding.equalsIgnoreCase("ascii")) {
            return new String(data, StandardCharsets.US_ASCII);
        }
        if (encoding.equalsIgnoreCase("latin1") || encoding.equalsIgnoreCase("binary")) {
            return new String(data, StandardCharsets.ISO_8859_1);
        }
        throw new IllegalArgumentException("Unsupported encoding: " + encoding);
    }

    public void writeUtf8(String value) {
        if (value == null) return;
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        int len = Math.min(bytes.length, data.length);
        System.arraycopy(bytes, 0, data, 0, len);
    }

    public void write(String value, String encoding) {
        if (value == null) return;
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
        int len = Math.min(bytes.length, data.length);
        System.arraycopy(bytes, 0, data, 0, len);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= data.length) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + data.length);
        }
    }
}
