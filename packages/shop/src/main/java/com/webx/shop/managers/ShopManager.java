package com.webx.shop.managers;

import com.webx.shop.ShopPlugin;
import com.webx.shop.models.Shop;

import java.util.*;

public class ShopManager {
    private final ShopPlugin plugin;
    private final Map<String, Shop> shops;

    public ShopManager(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shops = new HashMap<>();
    }

    public void loadShops() {
        shops.clear();
        // TODO: Load from storage
        plugin.getLogger().info("Loaded " + shops.size() + " shops");
    }

    public void saveShops() {
        // TODO: Save to storage
        plugin.getLogger().info("Saved " + shops.size() + " shops");
    }

    public Shop createShop(String name, UUID owner) {
        if (shops.containsKey(name.toLowerCase())) {
            return null;
        }

        Shop shop = new Shop(name, owner);
        shops.put(name.toLowerCase(), shop);
        return shop;
    }

    public Shop getShop(String name) {
        return shops.get(name.toLowerCase());
    }

    public void deleteShop(String name) {
        shops.remove(name.toLowerCase());
    }

    public Collection<Shop> getAllShops() {
        return shops.values();
    }

    public int getShopCount() {
        return shops.size();
    }
}
