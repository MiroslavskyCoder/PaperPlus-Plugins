package com.webx.ranks.commands;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.managers.PlayerRankManager;
import com.webx.ranks.models.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command handler for displaying rank information: /rankinfo
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
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /rankinfo <rankId>");
            return true;
        }

        String rankId = args[0].toLowerCase();
        Rank rank = rankManager.getRank(rankId);

        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "Rank not found: " + rankId);
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "=== Rank: " + rank.getDisplayName() + " ===");
        sender.sendMessage(ChatColor.GREEN + "ID: " + rank.getId());
        sender.sendMessage(ChatColor.GREEN + "Priority: " + rank.getPriority());
        sender.sendMessage(ChatColor.GREEN + "Prefix: " + rankManager.formatPrefix(rank));
        sender.sendMessage(ChatColor.GREEN + "Purchasable: " + (rank.isPurchasable() ? "Yes" : "No"));
        
        if (rank.isPurchasable()) {
            sender.sendMessage(ChatColor.YELLOW + "Purchase Price: " + rank.getPurchasePrice() + " coins");
        }

        if (!rank.getPermissions().isEmpty()) {
            sender.sendMessage(ChatColor.AQUA + "Permissions:");
            rank.getPermissions().forEach(perm ->
                    sender.sendMessage(ChatColor.AQUA + "  - " + perm)
            );
        }

        if (!rank.getFeatures().isEmpty()) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Features:");
            rank.getFeatures().forEach((feature, enabled) ->
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "  - " + feature + ": " + (enabled ? "✓" : "✗"))
            );
        }

        return true;
    }
}
