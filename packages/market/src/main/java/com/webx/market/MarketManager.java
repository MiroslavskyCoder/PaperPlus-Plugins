package com.webx.market;

import org.bukkit.entity.Player;
import java.util.*;

public class MarketManager {
    private Map<String, MarketItem> items = new HashMap<>();
    
    public void addItem(String name, double buyPrice, double sellPrice) {
        items.put(name, new MarketItem(name, buyPrice, sellPrice));
    }
    
    public MarketItem getMarketItem(String name) {
        return items.get(name);
    }
    
    public Collection<MarketItem> getAllMarketItems() {
        return items.values();
    }
    
    static class MarketItem {
        String name;
        double buyPrice;
        double sellPrice;
        
        MarketItem(String name, double buyPrice, double sellPrice) {
            this.name = name;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
        }
    }
}
