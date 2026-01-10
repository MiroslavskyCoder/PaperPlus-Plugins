package com.webx.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * REST API service for Shop plugin configuration
 * Manages shop.json file through web dashboard
 */
public class ShopService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Path SHOP_CONFIG_PATH = Paths.get("plugins/Shop/shop.json");
    
    /**
     * GET /api/shop/config
     * Returns current shop configuration
     */
    public static void getShopConfig(Context ctx) {
        try {
            if (!Files.exists(SHOP_CONFIG_PATH)) {
                // Create default config if doesn't exist
                Files.createDirectories(SHOP_CONFIG_PATH.getParent());
                Files.writeString(SHOP_CONFIG_PATH, "[]");
            }
            
            String content = Files.readString(SHOP_CONFIG_PATH);
            List<ShopItem> items = mapper.readValue(content, 
                mapper.getTypeFactory().constructCollectionType(List.class, ShopItem.class));
            
            ctx.json(Map.of(
                "success", true,
                "items", items
            ));
            
        } catch (IOException e) {
            ctx.status(500).json(Map.of(
                "error", "Failed to read shop config: " + e.getMessage(),
                "success", false
            ));
        }
    }
    
    /**
     * PUT /api/shop/config
     * Updates shop configuration
     */
    public static void updateShopConfig(Context ctx) {
        try {
            List<ShopItem> items = ctx.bodyAsClass(
                mapper.getTypeFactory().constructCollectionType(List.class, ShopItem.class));
            
            // Validate items
            for (ShopItem item : items) {
                if (item.name == null || item.material == null || item.price <= 0) {
                    ctx.status(400).json(Map.of(
                        "error", "Invalid item data: name, material, and price are required",
                        "success", false
                    ));
                    return;
                }
            }
            
            // Write to file
            Files.createDirectories(SHOP_CONFIG_PATH.getParent());
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(items);
            Files.writeString(SHOP_CONFIG_PATH, json);
            
            ctx.json(Map.of(
                "success", true,
                "message", "Shop config updated successfully",
                "itemCount", items.size()
            ));
            
        } catch (IOException e) {
            ctx.status(500).json(Map.of(
                "error", "Failed to update shop config: " + e.getMessage(),
                "success", false
            ));
        }
    }
    
    /**
     * POST /api/shop/item
     * Adds a new item to shop
     */
    public static void addShopItem(Context ctx) {
        try {
            ShopItem newItem = ctx.bodyAsClass(ShopItem.class);
            
            // Validate
            if (newItem.name == null || newItem.material == null || newItem.price <= 0) {
                ctx.status(400).json(Map.of(
                    "error", "Invalid item: name, material, and price are required",
                    "success", false
                ));
                return;
            }
            
            // Read existing items
            String content = Files.readString(SHOP_CONFIG_PATH);
            List<ShopItem> items = mapper.readValue(content, 
                mapper.getTypeFactory().constructCollectionType(List.class, ShopItem.class));
            
            // Add new item
            items.add(newItem);
            
            // Save
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(items);
            Files.writeString(SHOP_CONFIG_PATH, json);
            
            ctx.json(Map.of(
                "success", true,
                "message", "Item added successfully"
            ));
            
        } catch (IOException e) {
            ctx.status(500).json(Map.of(
                "error", "Failed to add item: " + e.getMessage(),
                "success", false
            ));
        }
    }
    
    /**
     * DELETE /api/shop/item/:id
     * Removes an item from shop
     */
    public static void deleteShopItem(Context ctx) {
        try {
            String itemId = ctx.pathParam("id");
            
            // Read existing items
            String content = Files.readString(SHOP_CONFIG_PATH);
            List<ShopItem> items = mapper.readValue(content, 
                mapper.getTypeFactory().constructCollectionType(List.class, ShopItem.class));
            
            // Remove item
            boolean removed = items.removeIf(item -> itemId.equals(item.id));
            
            if (!removed) {
                ctx.status(404).json(Map.of(
                    "error", "Item not found",
                    "success", false
                ));
                return;
            }
            
            // Save
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(items);
            Files.writeString(SHOP_CONFIG_PATH, json);
            
            ctx.json(Map.of(
                "success", true,
                "message", "Item deleted successfully"
            ));
            
        } catch (IOException e) {
            ctx.status(500).json(Map.of(
                "error", "Failed to delete item: " + e.getMessage(),
                "success", false
            ));
        }
    }
    
    /**
     * Shop item data model
     */
    public static class ShopItem {
        public String id;
        public String name;
        public String material;
        public double price;
        public String iconUrl;
        public String description;
        public int stock = -1; // -1 = unlimited
        
        public ShopItem() {}
    }
}
