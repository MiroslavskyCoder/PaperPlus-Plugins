package com.webx.ranks;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.managers.PlayerRankManager;
import com.webx.ranks.models.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Event listener for applying rank effects (prefixes, permissions, etc.)
 */
public class RankEventListener implements Listener {
    private final RankManager rankManager;
    private final PlayerRankManager playerRankManager;

    public RankEventListener(RankManager rankManager, PlayerRankManager playerRankManager) {
        this.rankManager = rankManager;
        this.playerRankManager = playerRankManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Ensure player rank data exists
        playerRankManager.getOrCreatePlayerRank(player.getUniqueId(), player.getName());
        
        // Update player's display name with rank prefix
        updatePlayerDisplayName(player);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String rankId = playerRankManager.getPlayerPrimaryRank(player.getUniqueId());
        Rank rank = rankManager.getRank(rankId);

        if (rank != null && !rank.getPrefix().isEmpty()) {
            String prefix = rankManager.formatPrefix(rank);
            event.setFormat(prefix + " §r" + player.getName() + ": " + event.getMessage());
        }
    }

    private void updatePlayerDisplayName(Player player) {
        String rankId = playerRankManager.getPlayerPrimaryRank(player.getUniqueId());
        Rank rank = rankManager.getRank(rankId);

        if (rank != null) {
            String tabListName = rankManager.formatTabListName(player.getName(), rank);
            player.setPlayerListName(tabListName);
            
            // Optional: Update display name in world
            // player.setDisplayName(tabListName);
        }
    }
}
