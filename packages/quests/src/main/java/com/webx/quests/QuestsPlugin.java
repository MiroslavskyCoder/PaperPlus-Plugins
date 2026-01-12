package com.webx.quests;

import com.webx.quests.api.QuestAPIController;
import com.webx.quests.commands.*;
import com.webx.quests.database.AsyncQuestDataManager;
import com.webx.quests.listeners.*;
import com.webx.quests.managers.*;
import com.webx.quests.utils.ConfigManager;
import com.webx.quests.utils.MessageManager;
import io.javalin.Javalin;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestsPlugin extends JavaPlugin {
    private static QuestsPlugin instance;
    
    private QuestManager questManager;
    private ProgressManager progressManager;
    private ObjectiveManager objectiveManager;
    private RewardManager rewardManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private AsyncQuestDataManager asyncDataManager;
    private Javalin javalinApp;
    private QuestAPIController apiController;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        
        questManager = new QuestManager(this);
        progressManager = new ProgressManager(this);
        objectiveManager = new ObjectiveManager(this);
        rewardManager = new RewardManager(this);
        asyncDataManager = new AsyncQuestDataManager(this);
        
        registerCommands();
        registerListeners();
        
        questManager.loadQuests();
        progressManager.loadProgress();
        
        // Start REST API server
        startAPIServer();
        
        getLogger().info("Quests plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (progressManager != null) {
            progressManager.saveProgress();
        }
        
        // Stop REST API server
        if (javalinApp != null) {
            javalinApp.stop();
        }
        
        getLogger().info("Quests plugin disabled!");
    }

    private void startAPIServer() {
        try {
            int port = getConfig().getInt("api.port", 7071);
            javalinApp = Javalin.create(config -> {
                config.showJavalinBanner = false;
            }).start(port);
            
            apiController = new QuestAPIController(this);
            apiController.registerRoutes(javalinApp);
            
            getLogger().info("Quest API server started on port " + port);
        } catch (Exception e) {
            getLogger().warning("Failed to start Quest API server: " + e.getMessage());
        }
    }

    private void registerCommands() {
        getCommand("quests").setExecutor(new QuestsCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerKillListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemCollectListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
    }

    public static QuestsPlugin getInstance() {
        return instance;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public ProgressManager getProgressManager() {
        return progressManager;
    }

    public ObjectiveManager getObjectiveManager() {
        return objectiveManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public AsyncQuestDataManager getAsyncDataManager() {
        return asyncDataManager;
    }
}
