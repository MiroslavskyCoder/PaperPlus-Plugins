package com.webx.quests;

import com.webx.quests.commands.*;
import com.webx.quests.listeners.*;
import com.webx.quests.managers.*;
import com.webx.quests.utils.ConfigManager;
import com.webx.quests.utils.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestsPlugin extends JavaPlugin {
    private static QuestsPlugin instance;
    
    private QuestManager questManager;
    private ProgressManager progressManager;
    private ObjectiveManager objectiveManager;
    private RewardManager rewardManager;
    private ConfigManager configManager;
    private MessageManager messageManager;

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
        
        registerCommands();
        registerListeners();
        
        questManager.loadQuests();
        progressManager.loadProgress();
        
        getLogger().info("Quests plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (progressManager != null) {
            progressManager.saveProgress();
        }
        
        getLogger().info("Quests plugin disabled!");
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
}
