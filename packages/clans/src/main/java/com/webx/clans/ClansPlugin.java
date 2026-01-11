package com.webx.clans;

import com.webx.clans.commands.*;
import com.webx.clans.listeners.*;
import com.webx.clans.managers.*;
import com.webx.clans.utils.ConfigManager;
import com.webx.clans.utils.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ClansPlugin extends JavaPlugin {
    private static ClansPlugin instance;
    
    private ClanManager clanManager;
    private MemberManager memberManager;
    private TerritoryManager territoryManager;
    private RankManager rankManager;
    private InviteManager inviteManager;
    private ClanPowerManager clanPowerManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ClanDisplayListener clanDisplayListener;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        
        clanManager = new ClanManager(this);
        memberManager = new MemberManager(this);
        territoryManager = new TerritoryManager(this);
        rankManager = new RankManager(this);
        inviteManager = new InviteManager(this);
        clanPowerManager = new ClanPowerManager(this, clanManager);
        clanDisplayListener = new ClanDisplayListener(this, clanManager);
        
        registerCommands();
        registerListeners();
        
        clanManager.loadClans();
        
        // Start clan power updates
        clanPowerManager.startUpdateTask();
        
        getLogger().info("Clans plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (clanManager != null) {
            clanManager.saveClans();
        }
        
        getLogger().info("Clans plugin disabled!");
    }

    private void registerCommands() {
        getCommand("clan").setExecutor(new ClanCommand(this));
        getCommand("topclans").setExecutor(new TopClansCommand(this, clanManager));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(clanDisplayListener, this);
    }

    public static ClansPlugin getInstance() {
        return instance;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public MemberManager getMemberManager() {
        return memberManager;
    }

    public TerritoryManager getTerritoryManager() {
        return territoryManager;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public InviteManager getInviteManager() {
        return inviteManager;
    }

    public ClanPowerManager getClanPowerManager() {
        return clanPowerManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}
