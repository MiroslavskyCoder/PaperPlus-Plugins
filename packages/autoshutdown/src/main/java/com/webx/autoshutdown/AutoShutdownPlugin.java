package com.webx.autoshutdown;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.redis.ConfigStandardConfig;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.List;

public class AutoShutdownPlugin extends JavaPlugin {
    
    private static final String REDIS_CONFIG_KEY = "autoshutdown:config";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean enabled;
    private int timeout;
    private List<Integer> warnings;
    private String shutdownMessage;
    
    private BukkitTask shutdownTask;
    private long emptyServerSince = -1;
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
        
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("autoshutdown").setExecutor(new AutoShutdownCommand(this));
        
        // Check every 20 ticks (1 second)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::checkForShutdown, 0L, 20L);
        
        getLogger().info("AutoShutdown plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        if (shutdownTask != null) {
            shutdownTask.cancel();
        }
        getLogger().info("AutoShutdown plugin disabled!");
        closeRedis();
    }
    
    public void loadConfiguration() {
        AutoShutdownConfig cfg = null;

        if (useRedis && redisIO != null) {
            cfg = loadConfigFromRedis();
        }

        if (cfg == null) {
            reloadConfig();
            cfg = new AutoShutdownConfig();
            cfg.enabled = getConfig().getBoolean("enabled", true);
            cfg.timeout = getConfig().getInt("timeout", 10);
            cfg.warnings = getConfig().getIntegerList("warnings");
            cfg.shutdownMessage = getConfig().getString("shutdown-message", "§cServer shutting down...");

            if (useRedis && redisIO != null) {
                saveConfigToRedis(cfg);
            }
        }

        enabled = cfg.enabled;
        timeout = cfg.timeout;
        warnings = cfg.warnings;
        shutdownMessage = cfg.shutdownMessage;
    }
    
    private void checkForShutdown() {
        if (!enabled) return;
        
        int playerCount = Bukkit.getOnlinePlayers().size();
        
        if (playerCount == 0) {
            if (emptyServerSince == -1) {
                emptyServerSince = System.currentTimeMillis();
                getLogger().info("Server is empty. Shutdown timer started.");
            }
            
            long emptyMinutes = (System.currentTimeMillis() - emptyServerSince) / 60000;
            
            // Check warnings
            for (int warningTime : warnings) {
                if (emptyMinutes == timeout - warningTime) {
                    getLogger().warning("Server will shutdown in " + warningTime + " minutes...");
                }
            }
            
            // Shutdown
            if (emptyMinutes >= timeout) {
                getLogger().info(shutdownMessage);
                Bukkit.getServer().shutdown();
            }
        } else {
            if (emptyServerSince != -1) {
                getLogger().info("Players online. Shutdown timer cancelled.");
                emptyServerSince = -1;
            }
        }
    }
    
    public boolean isAutoShutdownEnabled() {
        return enabled;
    }
    
    public void setAutoShutdownEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            emptyServerSince = -1;
        }
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public long getEmptyServerSince() {
        return emptyServerSince;
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

    private AutoShutdownConfig loadConfigFromRedis() {
        try {
            return redisIO.getJson(REDIS_CONFIG_KEY)
                    .map(json -> gson.fromJson(json, AutoShutdownConfig.class))
                    .orElse(null);
        } catch (Exception e) {
            getLogger().warning("Failed to load AutoShutdown config from Redis: " + e.getMessage());
            return null;
        }
    }

    private void saveConfigToRedis(AutoShutdownConfig cfg) {
        try {
            redisIO.setJson(REDIS_CONFIG_KEY, gson.toJsonTree(cfg));
        } catch (Exception e) {
            getLogger().warning("Failed to save AutoShutdown config to Redis: " + e.getMessage());
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

    private static class AutoShutdownConfig {
        boolean enabled;
        int timeout;
        List<Integer> warnings;
        String shutdownMessage;
    }
}
