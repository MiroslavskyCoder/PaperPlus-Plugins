package com.webx.backtp;

import org.bukkit.plugin.java.JavaPlugin;

public class BackTpPlugin extends JavaPlugin {
    private BackManager backManager;
    private TpaManager tpaManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        backManager = new BackManager();
        teleportManager = new TeleportManager(this);
        tpaManager = new TpaManager(this, teleportManager);
        
        getCommand("back").setExecutor(new BackCommand(this, backManager, teleportManager));
        getCommand("tpa").setExecutor(new TpaCommands(this, tpaManager));
        getCommand("tpahere").setExecutor(new TpaCommands(this, tpaManager));
        getCommand("tpaccept").setExecutor(new TpaCommands(this, tpaManager));
        getCommand("tpdeny").setExecutor(new TpaCommands(this, tpaManager));
        
        getServer().getPluginManager().registerEvents(new DeathListener(backManager), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(this, teleportManager), this);
        
        getLogger().info("BackTP enabled!");
    }

    @Override
    public void onDisable() {
        teleportManager.cancelAll();
        tpaManager.cancelAll();
        getLogger().info("BackTP disabled!");
    }

    public BackManager getBackManager() {
        return backManager;
    }

    public TpaManager getTpaManager() {
        return tpaManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
}
