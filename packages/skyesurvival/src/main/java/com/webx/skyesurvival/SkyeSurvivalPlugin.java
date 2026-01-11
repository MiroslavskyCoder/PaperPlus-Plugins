package com.webx.skyesurvival;

import com.webx.skyesurvival.commands.SurvivalCommand;
import com.webx.skyesurvival.listeners.ResourceGatherListener;
import com.webx.skyesurvival.managers.SurvivalManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyeSurvivalPlugin extends JavaPlugin {
    private static SkyeSurvivalPlugin instance;
    private SurvivalManager survivalManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize manager
        this.survivalManager = new SurvivalManager();
        
        // Register commands
        getCommand("survival").setExecutor(new SurvivalCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new ResourceGatherListener(this), this);
        
        getLogger().info("SkyeSurvival Plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SkyeSurvival Plugin disabled!");
    }
    
    public static SkyeSurvivalPlugin getInstance() {
        return instance;
    }
    
    public SurvivalManager getSurvivalManager() {
        return survivalManager;
    }
}
