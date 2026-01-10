package com.webx.marketplace.managers;

import com.webx.marketplace.models.ShopItem;
import java.util.*;

public class MarketplaceManager {
    private final List<ShopItem> listings = new ArrayList<>();
    
    public void addListing(ShopItem item) {
        listings.add(item);
    }
    
    public List<ShopItem> getListings() {
        return new ArrayList<>(listings);
    }
    
    public void removeListing(ShopItem item) {
        listings.remove(item);
    }
}
