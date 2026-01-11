package com.webx.api.endpoints;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Shop Management API Endpoint
 * Allows managing shop items through Web Dashboard
 */
public class ShopEndpoint {
    private final JavaPlugin plugin;
    private final Gson gson;
    private final Path shopConfigFile;
    
    public ShopEndpoint(JavaPlugin plugin, Gson gson) {
        this.plugin = plugin;
        this.gson = gson;
        
        // Shop config file in Shop plugin folder
        File shopPluginFolder = new File(plugin.getDataFolder().getParentFile(), "Shop");
        shopPluginFolder.mkdirs();
        this.shopConfigFile = shopPluginFolder.toPath().resolve("shop.json");
        
        // Create default shop config if not exists
        if (!Files.exists(shopConfigFile)) {
            createDefaultShopConfig();
        }
    }
    
    private void createDefaultShopConfig() {
        List<ShopItem> defaultItems = Arrays.asList(
            new ShopItem("1", "Diamond Sword", "DIAMOND_SWORD", 100.0, null),
            new ShopItem("2", "Iron Armor Set", "IRON_CHESTPLATE", 50.0, null),
            new ShopItem("3", "Golden Apple", "GOLDEN_APPLE", 10.0, null)
        );
        
        saveShopItems(defaultItems);
        plugin.getLogger().info("Created default shop configuration");
    }
    
    /**
     * GET /api/shop
     * Get all shop items
     */
    public void getShopItems(Context ctx) {
        List<ShopItem> items = loadShopItems();
        ctx.json(Map.of(
            "success", true,
            "message", "Success",
            "data", items
        ));
    }
    
    /**
     * POST /api/shop
     * Add new shop item
     */
    public void addShopItem(Context ctx) {
        try {
            ShopItem newItem = gson.fromJson(ctx.body(), ShopItem.class);
            
            if (newItem.id == null || newItem.id.isEmpty()) {
                newItem.id = UUID.randomUUID().toString();
            }
            
            List<ShopItem> items = loadShopItems();
            items.add(newItem);
            
            saveShopItems(items);
            
            ctx.json(Map.of(
                "success", true,
                "message", "Item added successfully",
                "data", newItem
            ));
            
            plugin.getLogger().info("Added shop item: " + newItem.name);
            
        } catch (Exception e) {
            ctx.status(400).json(Map.of(
                "success", false,
                "message", "Invalid item data: " + e.getMessage()
            ));
        }
    }
    
    /**
     * GET /api/shop/{id}
     * Get specific shop item
     */
    public void getShopItem(Context ctx) {
        String id = ctx.pathParam("id");
        List<ShopItem> items = loadShopItems();
        
        Optional<ShopItem> item = items.stream()
                .filter(i -> i.id.equals(id))
                .findFirst();
        
        if (item.isPresent()) {
            ctx.json(Map.of(
                "success", true,
                "message", "Success",
                "data", item.get()
            ));
        } else {
            ctx.status(404).json(Map.of(
                "success", false,
                "message", "Item not found"
            ));
        }
    }
    
    /**
     * PUT /api/shop/{id}
     * Update shop item
     */
    public void updateShopItem(Context ctx) {
        String id = ctx.pathParam("id");
        
        try {
            ShopItem updatedItem = gson.fromJson(ctx.body(), ShopItem.class);
            updatedItem.id = id;
            
            List<ShopItem> items = loadShopItems();
            boolean found = false;
            
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).id.equals(id)) {
                    items.set(i, updatedItem);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                ctx.status(404).json(Map.of(
                    "success", false,
                    "message", "Item not found"
                ));
                return;
            }
            
            saveShopItems(items);
            
            ctx.json(Map.of(
                "success", true,
                "message", "Item updated successfully",
                "data", updatedItem
            ));
            
            plugin.getLogger().info("Updated shop item: " + updatedItem.name);
            
        } catch (Exception e) {
            ctx.status(400).json(Map.of(
                "success", false,
                "message", "Invalid item data: " + e.getMessage()
            ));
        }
    }
    
    /**
     * DELETE /api/shop/{id}
     * Delete shop item
     */
    public void deleteShopItem(Context ctx) {
        String id = ctx.pathParam("id");
        List<ShopItem> items = loadShopItems();
        
        boolean removed = items.removeIf(item -> item.id.equals(id));
        
        if (removed) {
            saveShopItems(items);
            ctx.json(Map.of(
                "success", true,
                "message", "Item deleted successfully"
            ));
            
            plugin.getLogger().info("Deleted shop item: " + id);
        } else {
            ctx.status(404).json(Map.of(
                "success", false,
                "message", "Item not found"
            ));
        }
    }
    
    private List<ShopItem> loadShopItems() {
        try (Reader reader = new FileReader(shopConfigFile.toFile())) {
            List<ShopItem> items = gson.fromJson(reader, new TypeToken<List<ShopItem>>(){}.getType());
            return items != null ? items : new ArrayList<>();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load shop config: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void saveShopItems(List<ShopItem> items) {
        try (Writer writer = new FileWriter(shopConfigFile.toFile())) {
            gson.toJson(items, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save shop config: " + e.getMessage());
        }
    }
    
    public static class ShopItem {
        public String id;
        public String name;
        public String material;
        public double price;
        public String icon;
        
        public ShopItem() {}
        
        public ShopItem(String id, String name, String material, double price, String icon) {
            this.id = id;
            this.name = name;
            this.material = material;
            this.price = price;
            this.icon = icon;
        }
    }
}
