package com.webx.mobcatch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.redis.ConfigStandardConfig;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MobCatchPlugin extends JavaPlugin {
    
    private static final String REDIS_CONFIG_KEY = "mobcatch:config";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean enabled;
    private boolean requirePermission;
    private String permission;
    private Set<EntityType> allowedMobs;
    private Set<EntityType> blacklistedMobs;
    private Map<String, String> messages;
    private String eggNameFormat;
    private boolean useRedis;
    private RedisIO redisIO;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.useRedis = "redis".equalsIgnoreCase(getConfig().getString("storage.type", "file"));
        if (useRedis) {
            initRedis();
        }
        loadConfiguration();
        
        getServer().getPluginManager().registerEvents(new MobCatchListener(this), this);
        
        getLogger().info("MobCatch plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("MobCatch plugin disabled!");
        closeRedis();
    }
    
    public void loadConfiguration() {
        MobCatchConfig cfg = null;

        if (useRedis && redisIO != null) {
            cfg = loadConfigFromRedis();
        }

        if (cfg == null) {
            reloadConfig();
            cfg = new MobCatchConfig();
            cfg.enabled = getConfig().getBoolean("enabled", true);
            cfg.requirePermission = getConfig().getBoolean("require-permission", true);
            cfg.permission = getConfig().getString("permission", "mobcatch.use");
            cfg.eggNameFormat = getConfig().getString("egg-name", "§6Captured %mob%");

            cfg.allowedMobs = new ArrayList<>(getConfig().getStringList("allowed-mobs"));
            cfg.blacklistedMobs = new ArrayList<>(getConfig().getStringList("blacklisted-mobs"));

            cfg.messages = new HashMap<>();
            if (getConfig().contains("messages") && getConfig().getConfigurationSection("messages") != null) {
                for (String key : getConfig().getConfigurationSection("messages").getKeys(false)) {
                    cfg.messages.put(key, getConfig().getString("messages." + key));
                }
            }

            if (useRedis && redisIO != null) {
                saveConfigToRedis(cfg);
            }
        }

        enabled = cfg.enabled;
        requirePermission = cfg.requirePermission;
        permission = cfg.permission;
        eggNameFormat = cfg.eggNameFormat;

        allowedMobs = new HashSet<>();
        if (cfg.allowedMobs != null) {
            for (String mobName : cfg.allowedMobs) {
                try {
                    allowedMobs.add(EntityType.valueOf(mobName.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    getLogger().warning("Unknown mob type: " + mobName);
                }
            }
        }

        blacklistedMobs = new HashSet<>();
        if (cfg.blacklistedMobs != null) {
            for (String mobName : cfg.blacklistedMobs) {
                try {
                    blacklistedMobs.add(EntityType.valueOf(mobName.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    getLogger().warning("Unknown mob type: " + mobName);
                }
            }
        }

        messages = cfg.messages != null ? cfg.messages : new HashMap<>();
    }
    
    public boolean isMobCatchEnabled() {
        return enabled;
    }
    
    public boolean isRequirePermission() {
        return requirePermission;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public Set<EntityType> getAllowedMobs() {
        return allowedMobs;
    }
    
    public Set<EntityType> getBlacklistedMobs() {
        return blacklistedMobs;
    }
    
    public String getMessage(String key) {
        return messages.getOrDefault(key, "§cMessage not found: " + key);
    }
    
    public String getEggNameFormat() {
        return eggNameFormat;
    }

    private void initRedis() {
        RedisConfig cfg = ConfigStandardConfig.load(getServerRoot(), new RedisConfig(), getLogger());
        try {
            redisIO = new RedisIO(cfg, getLogger());
            if (!redisIO.ping()) {
                getLogger().warning("Redis ping failed; falling back to file storage");
                redisIO = null;
            }
        } catch (Exception e) {
            getLogger().warning("Failed to initialize Redis: " + e.getMessage());
            redisIO = null;
        }
    }

    private MobCatchConfig loadConfigFromRedis() {
        try {
            return redisIO.getJson(REDIS_CONFIG_KEY)
                    .map(json -> gson.fromJson(json, MobCatchConfig.class))
                    .orElse(null);
        } catch (Exception e) {
            getLogger().warning("Failed to load MobCatch config from Redis: " + e.getMessage());
            return null;
        }
    }

    private void saveConfigToRedis(MobCatchConfig cfg) {
        try {
            redisIO.setJson(REDIS_CONFIG_KEY, gson.toJsonTree(cfg));
        } catch (Exception e) {
            getLogger().warning("Failed to save MobCatch config to Redis: " + e.getMessage());
        }
    }

    private File getServerRoot() {
        File pluginsDir = getDataFolder().getParentFile();
        return pluginsDir != null ? pluginsDir.getParentFile() : null;
    }

    private void closeRedis() {
        if (redisIO != null) {
            try {
                redisIO.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static class MobCatchConfig {
        boolean enabled;
        boolean requirePermission;
        String permission;
        String eggNameFormat;
        List<String> allowedMobs;
        List<String> blacklistedMobs;
        Map<String, String> messages;
    }
}
