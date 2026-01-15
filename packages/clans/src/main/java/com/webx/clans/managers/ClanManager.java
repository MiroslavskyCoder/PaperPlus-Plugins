package com.webx.clans.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webx.clans.ClansPlugin;
import com.webx.clans.models.Clan;
import com.webx.redis.ConfigStandardConfig;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class ClanManager {
    private final ClansPlugin plugin;
    private final Map<String, Clan> clans;
    private final Gson gson;
    private final File dataFile;
    private final boolean useRedis;
    private final String redisKey;
    private RedisIO redisIO;

    public ClanManager(ClansPlugin plugin) {
        this.plugin = plugin;
        this.clans = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataFile = new File(plugin.getDataFolder(), "clans.json");
        this.useRedis = plugin.getConfig().getBoolean("storage.redis.enabled", true);
        this.redisKey = plugin.getConfig().getString("storage.redis.key", "clans:data");

        if (useRedis) {
            this.redisIO = new RedisIO(loadRedisConfig(), plugin.getLogger());
            if (!redisIO.ping()) {
                plugin.getLogger().warning("Redis ping failed, falling back to file storage");
            }
        }
    }

    public void loadClans() {
        clans.clear();

        boolean loadedFromRedis = false;
        if (useRedis && redisIO != null) {
            loadedFromRedis = loadFromRedis();
        }

        if (!loadedFromRedis) {
            loadFromFile();
            if (useRedis && redisIO != null && !clans.isEmpty()) {
                // Seed Redis for faster future loads
                saveToRedis();
            }
        }
    }

    public void saveClans() {
        saveToFile();
        if (useRedis && redisIO != null) {
            saveToRedis();
        }
    }

    public void close() {
        if (redisIO != null) {
            redisIO.close();
        }
    }

    public Clan createClan(String name, UUID leader) {
        if (clans.containsKey(name.toLowerCase())) {
            return null;
        }

        Clan clan = new Clan(name, leader);
        clans.put(name.toLowerCase(), clan);
        return clan;
    }

    public Clan getClan(String name) {
        return clans.get(name.toLowerCase());
    }

    public Clan getClanByMember(UUID uuid) {
        return clans.values().stream()
                .filter(clan -> clan.getMembers().contains(uuid))
                .findFirst()
                .orElse(null);
    }

    public void deleteClan(String name) {
        clans.remove(name.toLowerCase());
    }

    public Collection<Clan> getAllClans() {
        return clans.values();
    }

    public int getClanCount() {
        return clans.size();
    }

    private boolean loadFromRedis() {
        try {
            Optional<String> payload = redisIO.getString(redisKey);
            if (payload.isEmpty() || payload.get().isBlank()) {
                return false;
            }

            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> dataList = gson.fromJson(payload.get(), listType);
            if (dataList == null) {
                return false;
            }

            for (Map<String, Object> data : dataList) {
                try {
                    Clan clan = Clan.fromMap(data);
                    clans.put(clan.getName().toLowerCase(), clan);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load clan from Redis: " + e.getMessage());
                }
            }

            plugin.getLogger().info("Loaded " + clans.size() + " clans from Redis");
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Redis load failed: " + e.getMessage());
            return false;
        }
    }

    private void loadFromFile() {
        if (!dataFile.exists()) {
            plugin.getLogger().info("No clans data file found, starting fresh");
            return;
        }

        try (Reader reader = new FileReader(dataFile)) {
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> dataList = gson.fromJson(reader, listType);

            if (dataList != null) {
                for (Map<String, Object> data : dataList) {
                    try {
                        Clan clan = Clan.fromMap(data);
                        clans.put(clan.getName().toLowerCase(), clan);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to load clan: " + e.getMessage());
                    }
                }
            }

            plugin.getLogger().info("Loaded " + clans.size() + " clans from file");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load clans: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try {
            if (!dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }

            List<Map<String, Object>> dataList = new ArrayList<>();
            for (Clan clan : clans.values()) {
                dataList.add(clan.toMap());
            }

            try (Writer writer = new FileWriter(dataFile)) {
                gson.toJson(dataList, writer);
            }

            plugin.getLogger().info("Saved " + clans.size() + " clans to file");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save clans: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveToRedis() {
        try {
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (Clan clan : clans.values()) {
                dataList.add(clan.toMap());
            }
            redisIO.setString(redisKey, gson.toJson(dataList));
            plugin.getLogger().info("Saved " + clans.size() + " clans to Redis");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save clans to Redis: " + e.getMessage());
        }
    }

    private RedisConfig loadRedisConfig() {
        boolean useRoot = plugin.getConfig().getBoolean("storage.redis.use-root-config", true);
        File serverRoot = plugin.getDataFolder().getParentFile() != null
                ? plugin.getDataFolder().getParentFile().getParentFile()
                : null;

        RedisConfig cfg = useRoot
                ? ConfigStandardConfig.load(serverRoot, new RedisConfig(), plugin.getLogger())
                : new RedisConfig();

        cfg.host = plugin.getConfig().getString("storage.redis.host", cfg.host);
        cfg.port = plugin.getConfig().getInt("storage.redis.port", cfg.port);
        cfg.password = plugin.getConfig().getString("storage.redis.password", cfg.password);
        cfg.database = plugin.getConfig().getInt("storage.redis.database", cfg.database);
        return cfg;
    }
}
