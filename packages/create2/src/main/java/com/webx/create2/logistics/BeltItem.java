package com.webx.create2.logistics;

import org.bukkit.inventory.ItemStack;

/**
 * Represents an item traveling on a belt.
 */
public class BeltItem {
    private ItemStack stack;
    private double progress; // 0..1 across the belt

    public BeltItem(ItemStack stack) {
        this.stack = stack;
        this.progress = 0.0;
    }

    public ItemStack getStack() {
        return stack;
    }

    public double getProgress() {
        return progress;
    }

    public void advance(double delta) {
        this.progress += delta;
    }

    public void resetProgress() {
        this.progress = 0.0;
    }
}
