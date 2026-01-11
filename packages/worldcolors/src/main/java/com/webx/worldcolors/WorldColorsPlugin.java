package com.webx.worldcolors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class WorldColorsPlugin extends JavaPlugin {
    
    private Map<String, WorldColorConfig> worldColors = new HashMap<>();
    private WorldColorConfig defaultConfig;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadWorldColors();
        
        getServer().getPluginManager().registerEvents(new WorldColorListener(this), this);
        getCommand("worldcolors").setExecutor(new WorldColorsCommand(this));
        
        // Update player names every 20 ticks
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::updateAllPlayerNames, 0L, 20L);
        
        getLogger().info("WorldColors plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("WorldColors plugin disabled!");
    }
    
    public void loadWorldColors() {
        worldColors.clear();
        reloadConfig();
        
        var worldsSection = getConfig().getConfigurationSection("worlds");
        if (worldsSection != null) {
            for (String worldName : worldsSection.getKeys(false)) {
                String colorName = getConfig().getString("worlds." + worldName + ".color", "WHITE");
                String prefix = getConfig().getString("worlds." + worldName + ".prefix", "");
                
                ChatColor color = ChatColor.valueOf(colorName.toUpperCase());
                worldColors.put(worldName, new WorldColorConfig(color, prefix));
            }
        }
        
        String defaultColor = getConfig().getString("default.color", "WHITE");
        String defaultPrefix = getConfig().getString("default.prefix", "");
        defaultConfig = new WorldColorConfig(ChatColor.valueOf(defaultColor), defaultPrefix);
    }
    
    public WorldColorConfig getWorldConfig(String worldName) {
        return worldColors.getOrDefault(worldName, defaultConfig);
    }
    
    private void updateAllPlayerNames() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerDisplay(player);
        }
    }
    
    public void updatePlayerDisplay(Player player) {
        String worldName = player.getWorld().getName();
        WorldColorConfig config = getWorldConfig(worldName);
        
        String displayName = config.getColor() + player.getName() + ChatColor.RESET;
        player.setDisplayName(displayName);
        player.setPlayerListName(config.getPrefix() + " " + displayName);
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
