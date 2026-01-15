package com.webx.simpleheal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.redis.ConfigStandardConfig;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleHealPlugin extends JavaPlugin {
    
    private static final String REDIS_CONFIG_KEY = "simpleheal:config";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean healHealth;
    private boolean healFood;
    private boolean clearEffects;
    private boolean clearFire;
    private int cooldown;
    
    private Map<String, String> messages;
    private Map<UUID, Long> cooldowns;
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
        
        cooldowns = new HashMap<>();
        
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("healall").setExecutor(new HealAllCommand(this));
        
        getLogger().info("SimpleHeal plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SimpleHeal plugin disabled!");
        closeRedis();
    }
    
    public void loadConfiguration() {
        SimpleHealConfig cfg = null;

        if (useRedis && redisIO != null) {
            cfg = loadConfigFromRedis();
        }

        if (cfg == null) {
            reloadConfig();
            cfg = new SimpleHealConfig();
            cfg.healHealth = getConfig().getBoolean("heal-health", true);
            cfg.healFood = getConfig().getBoolean("heal-food", true);
            cfg.clearEffects = getConfig().getBoolean("clear-effects", true);
            cfg.clearFire = getConfig().getBoolean("clear-fire", true);
            cfg.cooldown = getConfig().getInt("cooldown", 60);
            cfg.messages = new HashMap<>();
            if (getConfig().getConfigurationSection("messages") != null) {
                for (String key : getConfig().getConfigurationSection("messages").getKeys(false)) {
                    cfg.messages.put(key, getConfig().getString("messages." + key));
                }
            }

            if (useRedis && redisIO != null) {
                saveConfigToRedis(cfg);
            }
        }

        healHealth = cfg.healHealth;
        healFood = cfg.healFood;
        clearEffects = cfg.clearEffects;
        clearFire = cfg.clearFire;
        cooldown = cfg.cooldown;
        messages = cfg.messages != null ? cfg.messages : new HashMap<>();
    }
    
    public boolean isHealHealth() {
        return healHealth;
    }
    
    public boolean isHealFood() {
        return healFood;
    }
    
    public boolean isClearEffects() {
        return clearEffects;
    }
    
    public boolean isClearFire() {
        return clearFire;
    }
    
    public int getCooldown() {
        return cooldown;
    }
    
    public String getMessage(String key) {
        return messages.getOrDefault(key, "§cMessage not found: " + key);
    }
    
    public Map<UUID, Long> getCooldowns() {
        return cooldowns;
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

    private SimpleHealConfig loadConfigFromRedis() {
        try {
            return redisIO.getJson(REDIS_CONFIG_KEY)
                    .map(json -> gson.fromJson(json, SimpleHealConfig.class))
                    .orElse(null);
        } catch (Exception e) {
            getLogger().warning("Failed to load SimpleHeal config from Redis: " + e.getMessage());
            return null;
        }
    }

    private void saveConfigToRedis(SimpleHealConfig cfg) {
        try {
            redisIO.setJson(REDIS_CONFIG_KEY, gson.toJsonTree(cfg));
        } catch (Exception e) {
            getLogger().warning("Failed to save SimpleHeal config to Redis: " + e.getMessage());
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

    private static class SimpleHealConfig {
        boolean healHealth;
        boolean healFood;
        boolean clearEffects;
        boolean clearFire;
        int cooldown;
        Map<String, String> messages;
    }
}
