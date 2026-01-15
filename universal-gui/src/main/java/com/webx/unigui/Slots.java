package com.webx.unigui;

/** Slot math helpers (row/col → index). */
public final class Slots {
    private Slots() {}

    /** Zero-based row/col to inventory slot index. */
    public static int at(int row, int col) {
        return row * 9 + col;
    }

    /** Clamp slot into inventory bounds. */
    public static int clamp(int slot, int size) {
        return Math.max(0, Math.min(slot, size - 1));
    }
}
