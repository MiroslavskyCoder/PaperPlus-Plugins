package com.webx.shop.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webx.shop.ShopPlugin;
import com.webx.shop.models.Shop;
import com.webx.shop.models.ShopItem;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

public class ShopManager {
    private final ShopPlugin plugin;
    private final Map<String, Shop> shops;
    private final List<ShopItem> shopItems;
    private final Gson gson;

    public ShopManager(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shops = new HashMap<>();
        this.shopItems = new ArrayList<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void loadShops() {
        shops.clear();
        shopItems.clear();
        
        loadShopItems();
        
        plugin.getLogger().info("Loaded " + shopItems.size() + " shop items");
    }

    private void loadShopItems() {
        File shopFile = new File(plugin.getDataFolder(), "shop.json");
        
        // Copy default shop.json from resources if it doesn't exist
        if (!shopFile.exists()) {
            try {
                plugin.saveResource("shop.json", false);
                plugin.getLogger().info("Created default shop.json");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to create shop.json: " + e.getMessage());
                return;
            }
        }

        try (Reader reader = new FileReader(shopFile)) {
            Type listType = new TypeToken<List<ShopItem>>(){}.getType();
            List<ShopItem> items = gson.fromJson(reader, listType);
            
            if (items != null) {
                shopItems.addAll(items);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load shop items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveShops() {
        // TODO: Save custom shops to storage
        plugin.getLogger().info("Saved " + shops.size() + " shops");
    }

    public List<ShopItem> getShopItems() {
        return new ArrayList<>(shopItems);
    }

    public ShopItem getShopItem(String id) {
        return shopItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
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
