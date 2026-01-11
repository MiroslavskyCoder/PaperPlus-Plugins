package com.webx.marketplace.models;

public class Price {
    private double buyPrice;
    private double sellPrice;
    
    public Price(double buyPrice, double sellPrice) {
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
    
    public double getBuyPrice() { return buyPrice; }
    public double getSellPrice() { return sellPrice; }
    public void setBuyPrice(double price) { this.buyPrice = price; }
    public void setSellPrice(double price) { this.sellPrice = price; }
}
