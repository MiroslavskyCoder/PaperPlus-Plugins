package com.webx.worldcolors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorldColorsPlugin extends JavaPlugin {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, WorldColorConfig> worldColors = new HashMap<>();
    private WorldColorConfig defaultConfig;
    private PluginConfig config;
    
    @Override
    public void onEnable() {
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
    }
    
    public void loadConfiguration() {
        File configDir = new File("plugins/WebX/configs");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        File configFile = new File(configDir, "worldcolors.json");
        
        if (!configFile.exists()) {
            // Create default config
            config = new PluginConfig();
            config.enabled = true;
            config.updateInterval = 20;
            config.worldColors = new HashMap<>();
            config.worldColors.put("world", "GREEN");
            config.worldColors.put("world_nether", "RED");
            config.worldColors.put("world_the_end", "LIGHT_PURPLE");
            saveConfiguration();
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, PluginConfig.class);
            } catch (IOException e) {
                getLogger().severe("Failed to load config: " + e.getMessage());
                config = new PluginConfig();
            }
        }
        
        loadWorldColors();
    }
    
    private void saveConfiguration() {
        File configFile = new File("plugins/WebX/configs/worldcolors.json");
        try (FileWriter writer = new FileWriter(configFile)) {
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
}
