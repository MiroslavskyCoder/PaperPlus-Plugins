package com.webx.autoshutdown;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class AutoShutdownPlugin extends JavaPlugin {
    
    private boolean enabled;
    private int timeout;
    private List<Integer> warnings;
    private String shutdownMessage;
    
    private BukkitTask shutdownTask;
    private long emptyServerSince = -1;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
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
    }
    
    public void loadConfiguration() {
        reloadConfig();
        enabled = getConfig().getBoolean("enabled", true);
        timeout = getConfig().getInt("timeout", 10);
        warnings = getConfig().getIntegerList("warnings");
        shutdownMessage = getConfig().getString("shutdown-message", "§cServer shutting down...");
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
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
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
}
