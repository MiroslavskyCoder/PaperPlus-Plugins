package com.webx.shop.models;

import org.bukkit.inventory.ItemStack;

public class ShopItem {
    private final String id;
    private final ItemStack item;
    private double buyPrice;
    private double sellPrice;
    private int stock;

    public ShopItem(String id, ItemStack item, double buyPrice, double sellPrice, int stock) {
        this.id = id;
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public ItemStack getItem() {
        return item;
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
}
