package com.webx.marketplace.api;

import com.webx.marketplace.models.ShopItem;
import java.util.List;

public class MarketplaceAPI {
    
    public static List<ShopItem> getAvailableItems() {
        // Returns available marketplace items
        return List.of();
    }
    
    public static boolean purchaseItem(String itemName) {
        // Handles item purchase
        return true;
    }
}
