package com.webx.economy;

import com.webx.economy.commands.*;
import com.webx.economy.listeners.PlayerJoinListener;
import com.webx.economy.listeners.PlayerQuitListener;
import com.webx.economy.managers.*;
import com.webx.economy.storage.JsonStorage;
import com.webx.economy.storage.MySQLStorage;
import com.webx.economy.storage.StorageProvider;
import com.webx.economy.storage.YamlStorage;
import com.webx.economy.utils.ConfigManager;
import com.webx.economy.utils.MessageManager;
import com.webx.unigui.GuiService;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyPlugin extends JavaPlugin {
    private static EconomyPlugin instance;
    
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    private BankManager bankManager;
    private BalTopManager balTopManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private StorageProvider storage;
    private GuiService guiService;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        
        initStorage();
        
        accountManager = new AccountManager(this, storage);
        transactionManager = new TransactionManager(this);
        bankManager = new BankManager(this);
        balTopManager = new BalTopManager(this);
        guiService = new GuiService(this);
        guiService.registerEvents();
        
        registerCommands();
        registerListeners();
        
        accountManager.loadAccounts();
        balTopManager.startUpdateTask();
        
        getLogger().info("Economy plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (balTopManager != null) {
            balTopManager.stopUpdateTask();
        }
        
        if (accountManager != null) {
            accountManager.saveAccounts();
        }
        
        if (storage != null) {
            storage.close();
        }
        
        getLogger().info("Economy plugin disabled!");
    }

    private void initStorage() {
        String type = getConfig().getString("database.type", "json");
        
        switch (type.toLowerCase()) {
            case "mysql":
                storage = new MySQLStorage(this);
                break;
            case "yaml":
                storage = new YamlStorage(this);
                break;
            case "json":
            default:
                storage = new JsonStorage(getDataFolder());
                break;
        }
        
        storage.initialize();
    }

    private void registerCommands() {
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("baltop").setExecutor(new BalTopCommand(this));
        getCommand("bank").setExecutor(new BankCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("topcoins").setExecutor(new TopCoinsCommand(this, accountManager));
        getCommand("econgui").setExecutor(new com.webx.economy.commands.EconomyDashboardCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }

    public static EconomyPlugin getInstance() {
        return instance;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public BalTopManager getBalTopManager() {
        return balTopManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public GuiService getGuiService() {
        return guiService;
    }
}
