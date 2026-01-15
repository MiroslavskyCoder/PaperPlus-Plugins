package com.webx.playerinfo;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInfoPlugin extends JavaPlugin {
    
    private PlayerInfoListener playerInfoListener;
    private EconomyDataManager economyDataManager;
    private SidebarManager sidebarManager;
    
    @Override
    public void onEnable() {
        getLogger().info("PlayerInfo plugin enabled!");
        
        // Initialize managers
        economyDataManager = new EconomyDataManager(this);
        sidebarManager = new SidebarManager(this);
        
        playerInfoListener = new PlayerInfoListener(economyDataManager, sidebarManager);
        getServer().getPluginManager().registerEvents(playerInfoListener, this);
        
        // Update player info every 5 ticks (4 times per second)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            playerInfoListener.updateAllPlayersInfo();
        }, 0L, 5L);
        
        // Refresh economy data every 60 ticks (3 seconds)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            economyDataManager.refreshAccounts();
        }, 0L, 60L);
    }
    
    @Override
    public void onDisable() {
        getLogger().info("PlayerInfo plugin disabled!");
    }
}
