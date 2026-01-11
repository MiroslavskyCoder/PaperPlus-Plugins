package com.webx.marketplace.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final UUID buyerId;
    private final UUID sellerId;
    private final String itemName;
    private final double price;
    private final LocalDateTime timestamp;
    
    public Transaction(UUID buyerId, UUID sellerId, String itemName, double price) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.itemName = itemName;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }
    
    public UUID getBuyerId() { return buyerId; }
    public UUID getSellerId() { return sellerId; }
    public String getItemName() { return itemName; }
    public double getPrice() { return price; }
}
