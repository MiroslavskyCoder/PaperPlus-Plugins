package com.webx.warps;

import com.webx.warps.commands.*;
import com.webx.warps.listeners.*;
import com.webx.warps.managers.*;
import com.webx.warps.storage.*;
import com.webx.warps.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpsPlugin extends JavaPlugin {
    private static WarpsPlugin instance;
    
    private WarpManager warpManager;
    private TeleportManager teleportManager;
    private CooldownManager cooldownManager;
    private EconomyManager economyManager;
    private ConfigManager configManager;
    private StorageProvider storage;
    private MessageManager messageManager;
    private PermissionManager permissionManager;
    private GUIManager guiManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        permissionManager = new PermissionManager(this);
        
        // Initialize storage
        initStorage();
        
        // Initialize core managers
        warpManager = new WarpManager(this, storage);
        teleportManager = new TeleportManager(this);
        cooldownManager = new CooldownManager(this);
        economyManager = new EconomyManager(this);
        guiManager = new GUIManager(this);
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        // Load warps
        warpManager.loadWarps();
        
        getLogger().info("Warps plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (warpManager != null) {
            warpManager.saveWarps();
        }
        
        if (teleportManager != null) {
            teleportManager.cancelAll();
        }
        
        if (storage != null) {
            storage.close();
        }
        
        getLogger().info("Warps plugin disabled!");
    }
    
    private void initStorage() {
        String type = getConfig().getString("database.type", "yaml");
        
        if (type.equalsIgnoreCase("mysql")) {
            storage = new MySQLStorage(this);
        } else {
            storage = new YamlStorage(this);
        }
        
        storage.initialize();
    }
    
    private void registerCommands() {
        CommandRegistry registry = new CommandRegistry(this);
        registry.register("warp", new WarpCommand(this));
        registry.register("setwarp", new SetWarpCommand(this));
        registry.register("delwarp", new DelWarpCommand(this));
        registry.register("warps", new WarpsCommand(this));
        registry.register("warpinfo", new WarpInfoCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }
    
    public static WarpsPlugin getInstance() {
        return instance;
    }
    
    public WarpManager getWarpManager() {
        return warpManager;
    }
    
    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
    
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
    
    public GUIManager getGUIManager() {
        return guiManager;
    }
    
    public StorageProvider getStorage() {
        return storage;
    }
}
