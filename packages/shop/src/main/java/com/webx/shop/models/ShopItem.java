package com.webx.shop.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItem {
    private final String id;
    private final String name;
    private final String material;
    private final int amount;
    private double buyPrice;
    private double sellPrice;
    private int stock;
    private String[] lore;
    private String permission;

    public ShopItem(String id, String name, String material, int amount, double buyPrice, double sellPrice, int stock) {
        this.id = id;
        this.name = name;
        this.material = material;
        // Guard against zero/negative amounts coming from config to avoid IllegalArgumentException in ItemStack
        this.amount = Math.max(1, amount);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = Math.max(0, stock);
    }

    public void addStock(int amount) {
        this.stock += amount;
    }

    public boolean removeStock(int amount) {
        if (stock < amount) return false;
        stock -= amount;
        return true;
    }

    public boolean canBuy() {
        return buyPrice > 0;
    }

    public boolean canSell() {
        return sellPrice > 0;
    }

    public String[] getLore() {
        return lore;
    }

    public void setLore(String[] lore) {
        this.lore = lore;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Create an ItemStack from this ShopItem
     */
    public ItemStack toItemStack() {
        Material mat;
        try {
            mat = Material.valueOf(material.toUpperCase());
        } catch (IllegalArgumentException e) {
            mat = Material.STONE;
        }
        int safeAmount = Math.max(1, amount);
        return new ItemStack(mat, safeAmount);
    }
}
