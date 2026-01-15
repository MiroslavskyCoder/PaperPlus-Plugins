package com.webx.shop.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webx.shop.ShopPlugin;
import com.webx.shop.models.Shop;
import com.webx.shop.models.ShopItem;
import com.webx.redis.ConfigStandardConfig;
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
    private static final Type SHOP_LIST_TYPE = new TypeToken<List<Shop>>(){}.getType();
    private final ShopPlugin plugin;
    private final Map<String, Shop> shops;
    private final List<ShopItem> shopItems;
    private final Path shopConfigPath;
    private final Path shopsDataPath;
    private final boolean useRedis;
    private final String redisKey;
    private final String redisShopsKey;
    private RedisIO redisIO;
    private final Gson gson;

    public ShopManager(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shops = new HashMap<>();
        this.shopItems = new ArrayList<>();
        String filePath = plugin.getConfig().getString("storage.file.path", "shop.json");
        this.shopConfigPath = plugin.getDataFolder().toPath().resolve(filePath);
        String shopsFilePath = plugin.getConfig().getString("storage.file.shops-path", "shops.json");
        this.shopsDataPath = plugin.getDataFolder().toPath().resolve(shopsFilePath);
        this.redisKey = "shop:items";
        this.redisShopsKey = "shop:shops";
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        ConfigurationSection storage = plugin.getConfig().getConfigurationSection("storage");
        this.useRedis = storage != null && "redis".equalsIgnoreCase(storage.getString("type", "file"));
        if (useRedis) {
            initRedis(storage.getConfigurationSection("redis"));
        }
    }

    private void initRedis(ConfigurationSection redisSection) {
        RedisConfig cfg = loadRedisConfig(redisSection);

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

    private RedisConfig loadRedisConfig(ConfigurationSection redisSection) {
        File serverRoot = plugin.getDataFolder().getParentFile() != null
                ? plugin.getDataFolder().getParentFile().getParentFile()
                : null;

        RedisConfig cfg = ConfigStandardConfig.load(serverRoot, new RedisConfig(), plugin.getLogger());

        if (redisSection == null) {
            plugin.getLogger().warning("Redis storage selected but redis config section is missing");
            return cfg;
        }

        cfg.host = redisSection.getString("host", cfg.host);
        cfg.port = redisSection.getInt("port", cfg.port);
        cfg.password = redisSection.getString("password", cfg.password);
        cfg.database = redisSection.getInt("database", cfg.database);
        cfg.ssl = redisSection.getBoolean("ssl", cfg.ssl);
        cfg.timeoutMillis = redisSection.getInt("timeout-millis", cfg.timeoutMillis);
        cfg.socketTimeoutMillis = redisSection.getInt("socket-timeout-millis", cfg.socketTimeoutMillis);
        return cfg;
    }

    public void loadShops() {
        shops.clear();
        int itemCount = reloadShopItems();
        int shopCount = reloadCustomShops();
        plugin.getLogger().info("Loaded " + itemCount + " shop items and " + shopCount + " shops");
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

    private int reloadCustomShops() {
        List<Shop> loadedShops = new ArrayList<>();
        boolean loadedFromRedis = false;

        if (useRedis && redisIO != null) {
            loadedShops = readShopsFromRedis();
            loadedFromRedis = !loadedShops.isEmpty();
        }

        if (!loadedFromRedis) {
            ensureShopsFileExists();
            loadedShops = readShopsFromDisk();

            if (useRedis && redisIO != null && !loadedShops.isEmpty()) {
                writeShopsToRedis(loadedShops);
            }
        }

        synchronized (shops) {
            shops.clear();
            for (Shop shop : loadedShops) {
                shops.put(shop.getName().toLowerCase(), shop);
            }
            return shops.size();
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

    private void ensureShopsFileExists() {
        File parent = plugin.getDataFolder();
        if (!parent.exists() && !parent.mkdirs()) {
            plugin.getLogger().warning("Unable to create Shop data folder: " + parent);
        }

        File shopsFile = shopsDataPath.toFile();
        if (!shopsFile.exists()) {
            try {
                if (shopsFile.createNewFile()) {
                    java.nio.file.Files.writeString(shopsDataPath, "[]", StandardCharsets.UTF_8);
                    plugin.getLogger().info("Created empty shops.json file");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create shops.json: " + e.getMessage());
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
        List<Shop> currentShops;
        synchronized (shops) {
            currentShops = new ArrayList<>(shops.values());
        }

        if (useRedis && redisIO != null) {
            writeShopsToRedis(currentShops);
        }

        writeShopsToDisk(currentShops);
        plugin.getLogger().info("Saved " + currentShops.size() + " shops");
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

    private List<Shop> readShopsFromDisk() {
        try (Reader reader = new FileReader(shopsDataPath.toFile(), StandardCharsets.UTF_8)) {
            List<Shop> loaded = gson.fromJson(reader, SHOP_LIST_TYPE);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load shops from disk: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Shop> readShopsFromRedis() {
        try {
            return redisIO.getJson(redisShopsKey)
                    .map(json -> gson.<List<Shop>>fromJson(json, SHOP_LIST_TYPE))
                    .orElseGet(ArrayList::new);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load shops from Redis: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void writeShopsToRedis(List<Shop> currentShops) {
        try {
            redisIO.setJson(redisShopsKey, gson.toJsonTree(currentShops));
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save shops to Redis: " + e.getMessage());
        }
    }

    private void writeShopsToDisk(List<Shop> currentShops) {
        try {
            java.nio.file.Files.writeString(shopsDataPath, gson.toJson(currentShops), StandardCharsets.UTF_8);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save shops to disk: " + e.getMessage());
        }
    }
}
