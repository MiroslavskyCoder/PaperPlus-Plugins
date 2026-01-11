package com.webx.hometp;

import org.bukkit.plugin.java.JavaPlugin;

public class HomeTpPlugin extends JavaPlugin {
    private HomeManager homeManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        homeManager = new HomeManager(this);
        teleportManager = new TeleportManager(this);
        
        getCommand("sethome").setExecutor(new HomeCommands(this, homeManager, teleportManager));
        getCommand("delhome").setExecutor(new HomeCommands(this, homeManager, teleportManager));
        getCommand("home").setExecutor(new HomeCommands(this, homeManager, teleportManager));
        getCommand("homes").setExecutor(new HomeCommands(this, homeManager, teleportManager));
        
        getServer().getPluginManager().registerEvents(new TeleportListener(teleportManager), this);
        
        getLogger().info("HomeTP enabled!");
    }

    @Override
    public void onDisable() {
        homeManager.saveHomes();
        teleportManager.cancelAll();
        getLogger().info("HomeTP disabled!");
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
}
