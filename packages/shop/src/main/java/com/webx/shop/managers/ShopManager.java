package com.webx.shop.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webx.shop.ShopPlugin;
import com.webx.shop.models.Shop;
import com.webx.shop.models.ShopItem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopManager {
    private final ShopPlugin plugin;
    private final Map<String, Shop> shops;
    private final List<ShopItem> shopItems;
    private final Path shopConfigPath;
    private final Gson gson;

    public ShopManager(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shops = new HashMap<>();
        this.shopItems = new ArrayList<>();
        this.shopConfigPath = plugin.getDataFolder().toPath().resolve("shop.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void loadShops() {
        shops.clear();
        int count = reloadShopItems();
        plugin.getLogger().info("Loaded " + count + " shop items");
    }

    public int reloadShopItems() {
        ensureShopConfigExists();
        List<ShopItem> loadedItems = readShopItemsFromDisk();
        synchronized (shopItems) {
            shopItems.clear();
            shopItems.addAll(loadedItems);
            return shopItems.size();
        }
    }

    private void ensureShopConfigExists() {
        File parent = plugin.getDataFolder();
        if (!parent.exists() && !parent.mkdirs()) {
            plugin.getLogger().warning("Unable to create Shop data folder: " + parent);
        }

        File shopFile = shopConfigPath.toFile();
        if (!shopFile.exists()) {
            try {
                plugin.saveResource("shop.json", false);
                plugin.getLogger().info("Created default shop.json");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to create shop.json: " + e.getMessage());
            }
        }
    }

    private List<ShopItem> readShopItemsFromDisk() {
        Type listType = new TypeToken<List<ShopItem>>(){}.getType();
        try (Reader reader = new FileReader(shopConfigPath.toFile(), StandardCharsets.UTF_8)) {
            List<ShopItem> items = gson.fromJson(reader, listType);
            return items != null ? items : new ArrayList<>();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load shop items: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveShops() {
        // TODO: Save custom shops to storage
        plugin.getLogger().info("Saved " + shops.size() + " shops");
    }

    public List<ShopItem> getShopItems() {
        synchronized (shopItems) {
            return new ArrayList<>(shopItems);
        }
    }

    public ShopItem getShopItem(String id) {
        synchronized (shopItems) {
            return shopItems.stream()
                    .filter(item -> item.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }
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
