package com.webx.shop;

import com.webx.shop.commands.*;
import com.webx.shop.listeners.InventoryClickListener;
import com.webx.shop.managers.*;
import com.webx.shop.utils.ConfigManager;
import com.webx.shop.utils.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopPlugin extends JavaPlugin {
    private static ShopPlugin instance;
    
    private ShopManager shopManager;
    private InventoryManager inventoryManager;
    private TransactionManager transactionManager;
    private ConfigManager configManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        
        shopManager = new ShopManager(this);
        inventoryManager = new InventoryManager(this);
        transactionManager = new TransactionManager(this);
        
        registerCommands();
        registerListeners();
        
        shopManager.loadShops();
        
        getLogger().info("Shop plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (shopManager != null) {
            shopManager.saveShops();
        }
        
        getLogger().info("Shop plugin disabled!");
    }

    private void registerCommands() {
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("adminshop").setExecutor(new AdminShopCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
    }

    public static ShopPlugin getInstance() {
        return instance;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}
