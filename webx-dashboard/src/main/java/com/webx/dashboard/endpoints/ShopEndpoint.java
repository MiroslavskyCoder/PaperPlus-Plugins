package com.webx.dashboard.endpoints;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webx.dashboard.WebDashboardPlugin;
import com.webx.dashboard.api.WebApiServer.Response;
import io.javalin.http.Context;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ShopEndpoint {
    private final WebDashboardPlugin plugin;
    private final Gson gson;
    private final Path shopConfigFile;
    
    public ShopEndpoint(WebDashboardPlugin plugin, Gson gson) {
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
    }
    
    public void getShopItems(Context ctx) {
        List<ShopItem> items = loadShopItems();
        ctx.json(new Response(true, "Success", items));
    }
    
    public void updateShopItems(Context ctx) {
        try {
            ShopItem newItem = gson.fromJson(ctx.body(), ShopItem.class);
            
            List<ShopItem> items = loadShopItems();
            items.add(newItem);
            
            saveShopItems(items);
            
            ctx.json(new Response(true, "Item added successfully", newItem));
        } catch (Exception e) {
            ctx.status(400).json(new Response(false, "Invalid item data: " + e.getMessage()));
        }
    }
    
    public void getShopItem(Context ctx) {
        String id = ctx.pathParam("id");
        List<ShopItem> items = loadShopItems();
        
        Optional<ShopItem> item = items.stream()
                .filter(i -> i.id.equals(id))
                .findFirst();
        
        if (item.isPresent()) {
            ctx.json(new Response(true, "Success", item.get()));
        } else {
            ctx.status(404).json(new Response(false, "Item not found"));
        }
    }
    
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
                ctx.status(404).json(new Response(false, "Item not found"));
                return;
            }
            
            saveShopItems(items);
            ctx.json(new Response(true, "Item updated successfully", updatedItem));
            
        } catch (Exception e) {
            ctx.status(400).json(new Response(false, "Invalid item data: " + e.getMessage()));
        }
    }
    
    public void deleteShopItem(Context ctx) {
        String id = ctx.pathParam("id");
        List<ShopItem> items = loadShopItems();
        
        boolean removed = items.removeIf(item -> item.id.equals(id));
        
        if (removed) {
            saveShopItems(items);
            ctx.json(new Response(true, "Item deleted successfully"));
        } else {
            ctx.status(404).json(new Response(false, "Item not found"));
        }
    }
    
    private List<ShopItem> loadShopItems() {
        try (Reader reader = new FileReader(shopConfigFile.toFile())) {
            return gson.fromJson(reader, new TypeToken<List<ShopItem>>(){}.getType());
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
        
        public ShopItem(String id, String name, String material, double price, String icon) {
            this.id = id;
            this.name = name;
            this.material = material;
            this.price = price;
            this.icon = icon;
        }
    }
}
