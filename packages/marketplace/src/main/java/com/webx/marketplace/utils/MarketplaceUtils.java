package com.webx.marketplace.utils;

import com.webx.marketplace.models.ShopItem;
import java.util.List;

public class MarketplaceUtils {
    
    public static double calculateTotalPrice(List<ShopItem> items) {
        return items.stream().mapToDouble(ShopItem::getPrice).sum();
    }
    
    public static ShopItem findCheapest(List<ShopItem> items) {
        return items.stream().min((a, b) -> Double.compare(a.getPrice(), b.getPrice())).orElse(null);
    }
}
