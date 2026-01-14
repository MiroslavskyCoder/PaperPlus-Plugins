package com.webx.ranks;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.managers.PlayerRankManager;
import com.webx.ranks.commands.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Main plugin class for Ranks & Permissions System
 */
public class RanksPlugin extends JavaPlugin {
    private RankManager rankManager;
    private PlayerRankManager playerRankManager;

    @Override
    public void onEnable() {
        // Initialize managers
        rankManager = new RankManager(getDataFolder());
        playerRankManager = new PlayerRankManager(getDataFolder());
        
        // Initialize default permissions
        rankManager.initializeDefaultPermissions();

        // Register commands
        getCommand("rank").setExecutor(new RankCommand(rankManager, playerRankManager));
        getCommand("rankinfo").setExecutor(new RankInfoCommand(rankManager, playerRankManager));
        getCommand("ranklist").setExecutor(new RankListCommand(rankManager));

        // Register event listeners
        getServer().getPluginManager().registerEvents(
                new RankEventListener(rankManager, playerRankManager), this);

        // Schedule periodic tasks
        scheduleExpiredRankCheck();
        scheduleAutoSave();

        getLogger().info("✅ Ranks & Permissions System enabled!");
    }

    @Override
    public void onDisable() {
        if (playerRankManager != null) {
            playerRankManager.saveData();
        }
        if (rankManager != null) {
            rankManager.saveData();
        }
        getLogger().info("❌ Ranks & Permissions System disabled!");
    }

    private void scheduleExpiredRankCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                playerRankManager.checkAndRemoveExpiredRanks();
                
                // Notify players with expiring ranks
                var expiringRanks = playerRankManager.getExpiringRanks(86400000); // 24 hours
                for (var playerRank : expiringRanks) {
                    var player = getServer().getPlayer(playerRank.getPlayerId());
                    if (player != null) {
                        long hoursLeft = (playerRank.getExpiresAt() - System.currentTimeMillis()) / 3600000;
                        player.sendMessage("§e[Ranks] Your rank expires in " + hoursLeft + " hours!");
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 600L, 600L); // Every 30 seconds
    }

    private void scheduleAutoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                rankManager.saveData();
                playerRankManager.saveData();
            }
        }.runTaskTimerAsynchronously(this, 12000L, 12000L); // Every 10 minutes
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public PlayerRankManager getPlayerRankManager() {
        return playerRankManager;
    }
}
