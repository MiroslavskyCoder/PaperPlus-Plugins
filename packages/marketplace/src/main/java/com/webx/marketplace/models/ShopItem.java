package com.webx.marketplace.models;

import java.util.UUID;

public class ShopItem {
    private final UUID seller;
    private final String itemName;
    private final double price;
    private final int quantity;
    
    public ShopItem(UUID seller, String itemName, double price, int quantity) {
        this.seller = seller;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
    
    public UUID getSeller() { return seller; }
    public String getItemName() { return itemName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}
