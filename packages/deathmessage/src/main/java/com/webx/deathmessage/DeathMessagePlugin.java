package com.webx.deathmessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.redis.ConfigStandardConfig;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;

public class DeathMessagePlugin extends JavaPlugin {
    
    private static final String REDIS_CONFIG_KEY = "deathmessage:config";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean enabled;
    private boolean broadcast;
    private boolean showLocation;
    private boolean showWorld;
    private String messageFormat;
    private Map<String, String> causeMessages;
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
        
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        
        getLogger().info("DeathMessage plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("DeathMessage plugin disabled!");
        closeRedis();
    }
    
    public void loadConfiguration() {
        DeathMessageConfig cfg = null;

        if (useRedis && redisIO != null) {
            cfg = loadConfigFromRedis();
        }

        if (cfg == null) {
            reloadConfig();
            cfg = new DeathMessageConfig();
            cfg.enabled = getConfig().getBoolean("enabled", true);
            cfg.broadcast = getConfig().getBoolean("broadcast", true);
            cfg.showLocation = getConfig().getBoolean("show-location", true);
            cfg.showWorld = getConfig().getBoolean("show-world", false);
            cfg.messageFormat = getConfig().getString("message-format", "§c☠ §7%player% §cdied");
            cfg.causeMessages = new HashMap<>();
            if (getConfig().contains("causes") && getConfig().getConfigurationSection("causes") != null) {
                for (String key : getConfig().getConfigurationSection("causes").getKeys(false)) {
                    cfg.causeMessages.put(key, getConfig().getString("causes." + key));
                }
            }

            if (useRedis && redisIO != null) {
                saveConfigToRedis(cfg);
            }
        }

        enabled = cfg.enabled;
        broadcast = cfg.broadcast;
        showLocation = cfg.showLocation;
        showWorld = cfg.showWorld;
        messageFormat = cfg.messageFormat;
        causeMessages = cfg.causeMessages != null ? cfg.causeMessages : new HashMap<>();
    }
    
    public boolean isDeathMessageEnabled() {
        return enabled;
    }
    
    public boolean isBroadcast() {
        return broadcast;
    }
    
    public boolean isShowLocation() {
        return showLocation;
    }
    
    public boolean isShowWorld() {
        return showWorld;
    }
    
    public String getMessageFormat() {
        return messageFormat;
    }
    
    public Map<String, String> getCauseMessages() {
        return causeMessages;
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

    private DeathMessageConfig loadConfigFromRedis() {
        try {
            return redisIO.getJson(REDIS_CONFIG_KEY)
                    .map(json -> gson.fromJson(json, DeathMessageConfig.class))
                    .orElse(null);
        } catch (Exception e) {
            getLogger().warning("Failed to load DeathMessage config from Redis: " + e.getMessage());
            return null;
        }
    }

    private void saveConfigToRedis(DeathMessageConfig cfg) {
        try {
            redisIO.setJson(REDIS_CONFIG_KEY, gson.toJsonTree(cfg));
        } catch (Exception e) {
            getLogger().warning("Failed to save DeathMessage config to Redis: " + e.getMessage());
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

    private static class DeathMessageConfig {
        boolean enabled;
        boolean broadcast;
        boolean showLocation;
        boolean showWorld;
        String messageFormat;
        Map<String, String> causeMessages;
    }
}
