package com.webx.fromdrop;

import org.bukkit.Material;

public class DropEntry {
    private final Material material;
    private final int amount;
    private final double chance;

    public DropEntry(Material material, int amount, double chance) {
        this.material = material;
        this.amount = amount;
        this.chance = chance;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public double getChance() {
        return chance;
    }
}
