package com.webx.shop.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webx.shop.ShopPlugin;
import com.webx.shop.models.Shop;
import com.webx.shop.models.ShopItem;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;

public class ShopManager {
    private static final Type SHOP_ITEM_LIST_TYPE = new TypeToken<List<ShopItem>>(){}.getType();
    private final ShopPlugin plugin;
    private final Map<String, Shop> shops;
    private final List<ShopItem> shopItems;
    private final Path shopConfigPath;
    private final boolean useRedis;
    private final String redisKey;
    private RedisIO redisIO;
    private final Gson gson;

    public ShopManager(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shops = new HashMap<>();
        this.shopItems = new ArrayList<>();
        String filePath = plugin.getConfig().getString("storage.file.path", "shop.json");
        this.shopConfigPath = plugin.getDataFolder().toPath().resolve(filePath);
        this.redisKey = "shop:items";
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        ConfigurationSection storage = plugin.getConfig().getConfigurationSection("storage");
        this.useRedis = storage != null && "redis".equalsIgnoreCase(storage.getString("type", "file"));
        if (useRedis) {
            initRedis(storage.getConfigurationSection("redis"));
        }
    }

    private void initRedis(ConfigurationSection redisSection) {
        if (redisSection == null) {
            plugin.getLogger().warning("Redis storage selected but redis config section is missing");
            return;
        }

        RedisConfig cfg = new RedisConfig();
        cfg.host = redisSection.getString("host", "127.0.0.1");
        cfg.port = redisSection.getInt("port", 6379);
        cfg.password = redisSection.getString("password", "");
        cfg.database = redisSection.getInt("database", 0);
        cfg.ssl = redisSection.getBoolean("ssl", false);
        cfg.timeoutMillis = redisSection.getInt("timeout-millis", 2000);
        cfg.socketTimeoutMillis = redisSection.getInt("socket-timeout-millis", 2000);

        try {
            this.redisIO = new RedisIO(cfg, plugin.getLogger());
            if (!redisIO.ping()) {
                plugin.getLogger().warning("Redis ping failed; falling back to file storage");
                this.redisIO = null;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize Redis: " + e.getMessage());
            this.redisIO = null;
        }
    }

    public void loadShops() {
        shops.clear();
        int count = reloadShopItems();
        plugin.getLogger().info("Loaded " + count + " shop items");
    }

    public int reloadShopItems() {
        List<ShopItem> loadedItems = new ArrayList<>();
        boolean loadedFromRedis = false;

        if (useRedis && redisIO != null) {
            loadedItems = readShopItemsFromRedis();
            loadedFromRedis = !loadedItems.isEmpty();
        }

        if (!loadedFromRedis) {
            ensureShopConfigExists();
            loadedItems = readShopItemsFromDisk();

            if (useRedis && redisIO != null && !loadedItems.isEmpty()) {
                writeShopItemsToRedis(loadedItems);
            }
        }
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
        try (Reader reader = new FileReader(shopConfigPath.toFile(), StandardCharsets.UTF_8)) {
            List<ShopItem> items = gson.fromJson(reader, SHOP_ITEM_LIST_TYPE);
            return items != null ? items : new ArrayList<>();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load shop items: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<ShopItem> readShopItemsFromRedis() {
        try {
            return redisIO.getJson(redisKey)
                    .map(json -> gson.<List<ShopItem>>fromJson(json, SHOP_ITEM_LIST_TYPE))
                    .orElseGet(ArrayList::new);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load shop items from Redis: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void writeShopItemsToRedis(List<ShopItem> items) {
        try {
            redisIO.setJson(redisKey, gson.toJsonTree(items));
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save shop items to Redis: " + e.getMessage());
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

    public void close() {
        if (redisIO != null) {
            try {
                redisIO.close();
            } catch (Exception ignored) {
            }
        }
    }
}
