package com.webx.playerinfo;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInfoPlugin extends JavaPlugin {
    
    private PlayerInfoListener playerInfoListener;
    private EconomyDataManager economyDataManager;
    private SidebarManager sidebarManager;
    private PluginIntegrationService integrationService;
    
    @Override
    public void onEnable() {
        getLogger().info("PlayerInfo plugin enabled!");
        
        // Initialize managers
        economyDataManager = new EconomyDataManager(this);
        sidebarManager = new SidebarManager(this);
        integrationService = new PluginIntegrationService(this);
        
        playerInfoListener = new PlayerInfoListener(economyDataManager, sidebarManager, integrationService);
        getServer().getPluginManager().registerEvents(playerInfoListener, this);
        
        // Update player info every 5 ticks (4 times per second)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            playerInfoListener.updateAllPlayersInfo();
        }, 0L, 5L);
        
        // Refresh economy data every 60 ticks (3 seconds)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            economyDataManager.refreshAccounts();
        }, 0L, 60L);

        // Refresh shared cache every 10 seconds
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            integrationService.refreshCaches();
        }, 0L, 200L);
    }
    
    @Override
    public void onDisable() {
        getLogger().info("PlayerInfo plugin disabled!");
    }
}
