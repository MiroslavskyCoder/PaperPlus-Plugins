package com.webx.worldcolors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.redis.ConfigStandardConfig;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class WorldColorsPlugin extends JavaPlugin {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String REDIS_CONFIG_KEY = "worldcolors:config";
    private Map<String, WorldColorConfig> worldColors = new HashMap<>();
    private WorldColorConfig defaultConfig;
    private PluginConfig config;
    private boolean useRedis;
    private RedisIO redisIO;
    private Path configPath;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.useRedis = "redis".equalsIgnoreCase(getConfig().getString("storage.type", "file"));
        this.configPath = new File("plugins/WebX/configs/worldcolors.json").toPath();

        if (useRedis) {
            initRedis();
        }

        loadConfiguration();
        
        getServer().getPluginManager().registerEvents(new WorldColorListener(this), this);
        getCommand("worldcolors").setExecutor(new WorldColorsCommand(this));
        
        // Update player names every configured interval
        int interval = config != null && config.updateInterval > 0 ? config.updateInterval : 20;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::updateAllPlayerNames, 0L, interval);
        
        getLogger().info("WorldColors plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("WorldColors plugin disabled!");
        closeRedis();
    }
    
    public void loadConfiguration() {
        File configDir = configPath.getParent().toFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        File configFile = configPath.toFile();

        boolean loadedFromRedis = false;
        if (useRedis && redisIO != null) {
            loadedFromRedis = loadConfigFromRedis();
        }

        if (!loadedFromRedis) {
            if (!configFile.exists()) {
                createDefaultConfig();
            } else {
                try (FileReader reader = new FileReader(configFile, StandardCharsets.UTF_8)) {
                    config = GSON.fromJson(reader, PluginConfig.class);
                } catch (IOException e) {
                    getLogger().severe("Failed to load config: " + e.getMessage());
                    createDefaultConfig();
                }
            }

            if (useRedis && redisIO != null && config != null) {
                saveConfigToRedis(config);
            }
        }
        
        loadWorldColors();
    }
    
    private void saveConfiguration() {
        if (useRedis && redisIO != null && config != null) {
            saveConfigToRedis(config);
        }

        File configFile = configPath.toFile();
        try (FileWriter writer = new FileWriter(configFile, StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }
    
    public void loadWorldColors() {
        worldColors.clear();
        
        if (config.worldColors != null) {
            for (Map.Entry<String, String> entry : config.worldColors.entrySet()) {
                try {
                    ChatColor color = ChatColor.valueOf(entry.getValue().toUpperCase());
                    worldColors.put(entry.getKey(), new WorldColorConfig(color, ""));
                } catch (IllegalArgumentException e) {
                    getLogger().warning("Invalid color for world " + entry.getKey() + ": " + entry.getValue());
                }
            }
        }
        
        defaultConfig = new WorldColorConfig(ChatColor.WHITE, "");
    }
    
    public WorldColorConfig getWorldConfig(String worldName) {
        return worldColors.getOrDefault(worldName, defaultConfig);
    }
    
    private void updateAllPlayerNames() {
        if (config == null || !config.enabled) return;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerDisplay(player);
        }
    }
    
    public void updatePlayerDisplay(Player player) {
        if (config == null || !config.enabled) return;
        
        String worldName = player.getWorld().getName();
        WorldColorConfig worldConfig = getWorldConfig(worldName);
        
        String displayName = worldConfig.getColor() + player.getName() + ChatColor.RESET;
        player.setDisplayName(displayName);
        player.setPlayerListName(worldConfig.getPrefix() + " " + displayName);
    }
    
    public static class PluginConfig {
        public boolean enabled;
        public Map<String, String> worldColors;
        public int updateInterval;
    }
    
    public static class WorldColorConfig {
        private final ChatColor color;
        private final String prefix;
        
        public WorldColorConfig(ChatColor color, String prefix) {
            this.color = color;
            this.prefix = prefix;
        }
        
        public ChatColor getColor() {
            return color;
        }
        
        public String getPrefix() {
            return prefix;
        }
    }

    private void initRedis() {
        File serverRoot = getDataFolder().getParentFile() != null
                ? getDataFolder().getParentFile().getParentFile()
                : null;
        RedisConfig cfg = ConfigStandardConfig.load(serverRoot, new RedisConfig(), getLogger());

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

    private boolean loadConfigFromRedis() {
        try {
            return redisIO.getJson(REDIS_CONFIG_KEY)
                    .map(json -> GSON.fromJson(json, PluginConfig.class))
                    .map(cfg -> {
                        config = cfg;
                        return true;
                    })
                    .orElse(false);
        } catch (Exception e) {
            getLogger().warning("Failed to load worldcolors config from Redis: " + e.getMessage());
            return false;
        }
    }

    private void saveConfigToRedis(PluginConfig cfg) {
        try {
            redisIO.setJson(REDIS_CONFIG_KEY, GSON.toJsonTree(cfg));
        } catch (Exception e) {
            getLogger().warning("Failed to save worldcolors config to Redis: " + e.getMessage());
        }
    }

    private void createDefaultConfig() {
        config = new PluginConfig();
        config.enabled = true;
        config.updateInterval = 20;
        config.worldColors = new HashMap<>();
        config.worldColors.put("world", "GREEN");
        config.worldColors.put("world_nether", "RED");
        config.worldColors.put("world_the_end", "LIGHT_PURPLE");
        saveConfiguration();
    }

    private void closeRedis() {
        if (redisIO != null) {
            try {
                redisIO.close();
            } catch (Exception ignored) {
            }
        }
    }
}
