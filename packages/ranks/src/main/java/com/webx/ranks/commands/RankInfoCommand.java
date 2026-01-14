package com.webx.ranks.commands;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.managers.PlayerRankManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command handler for listing all available ranks: /ranklist
 */
public class RankInfoCommand implements CommandExecutor {
    private final RankManager rankManager;
    private final PlayerRankManager playerRankManager;

    public RankInfoCommand(RankManager rankManager, PlayerRankManager playerRankManager) {
        this.rankManager = rankManager;
        this.playerRankManager = playerRankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "========== Available Ranks ==========");
        
        rankManager.getRanksSortedByPriority().forEach(rank -> {
            String prefix = rankManager.formatPrefix(rank);
            String priceInfo = rank.isPurchasable() ? 
                    ChatColor.YELLOW + " (Price: " + rank.getPurchasePrice() + " coins)" : "";
            
            sender.sendMessage(prefix + " " + ChatColor.GREEN + rank.getDisplayName() + priceInfo);
            sender.sendMessage(ChatColor.GRAY + "  └─ ID: " + rank.getId());
        });
        
        sender.sendMessage(ChatColor.GOLD + "====================================");
        sender.sendMessage(ChatColor.YELLOW + "/rankinfo <rankId> - View rank details");
        
        return true;
    }
}
