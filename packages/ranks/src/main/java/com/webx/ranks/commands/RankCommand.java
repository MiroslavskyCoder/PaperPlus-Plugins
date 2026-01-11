package com.webx.ranks.commands;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.managers.PlayerRankManager;
import com.webx.ranks.models.Rank;
import com.webx.ranks.models.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Main command handler for rank management: /rank
 */
public class RankCommand implements CommandExecutor {
    private final RankManager rankManager;
    private final PlayerRankManager playerRankManager;

    public RankCommand(RankManager rankManager, PlayerRankManager playerRankManager) {
        this.rankManager = rankManager;
        this.playerRankManager = playerRankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "set":
                return handleSetRank(sender, args);
            case "give":
                return handleGiveRank(sender, args);
            case "remove":
                return handleRemoveRank(sender, args);
            case "check":
                return handleCheckRank(sender, args);
            case "info":
                return handleRankInfo(sender, args);
            case "create":
                return handleCreateRank(sender, args);
            case "edit":
                return handleEditRank(sender, args);
            case "list":
                return handleListRanks(sender, args);
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + subcommand);
                return false;
        }
    }

    private boolean handleSetRank(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rank.admin.manage-ranks")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /rank set <player> <rankId>");
            return true;
        }

        String playerName = args[1];
        String rankId = args[2];
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + playerName);
            return true;
        }

        Rank rank = rankManager.getRank(rankId);
        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "Rank not found: " + rankId);
            return true;
        }

        String reason = args.length > 3 ? String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length)) : "Admin assigned";
        playerRankManager.setPlayerPrimaryRank(player.getUniqueId(), rankId, 
                sender.getName(), reason);

        player.sendMessage(ChatColor.GREEN + "You have been assigned the rank: " + ChatColor.GOLD + rank.getDisplayName());
        sender.sendMessage(ChatColor.GREEN + "Set " + playerName + " to rank: " + rankId);
        return true;
    }

    private boolean handleGiveRank(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rank.admin.manage-ranks")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /rank give <player> <rankId> [duration in hours]");
            return true;
        }

        String playerName = args[1];
        String rankId = args[2];
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + playerName);
            return true;
        }

        Rank rank = rankManager.getRank(rankId);
        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "Rank not found: " + rankId);
            return true;
        }

        if (args.length > 3) {
            try {
                long hours = Long.parseLong(args[3]);
                playerRankManager.setPlayerRankWithExpiry(player.getUniqueId(), rankId,
                        hours * 3600000, sender.getName(), "Temporary rank grant");
                sender.sendMessage(ChatColor.GREEN + "Gave " + playerName + " temporary rank: " + rankId + " for " + hours + " hours");
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid duration!");
                return true;
            }
        } else {
            playerRankManager.setPlayerPrimaryRank(player.getUniqueId(), rankId,
                    sender.getName(), "Rank grant");
            sender.sendMessage(ChatColor.GREEN + "Gave " + playerName + " rank: " + rankId);
        }

        return true;
    }

    private boolean handleRemoveRank(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rank.admin.manage-ranks")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /rank remove <player>");
            return true;
        }

        String playerName = args[1];
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + playerName);
            return true;
        }

        playerRankManager.removePlayerRank(player.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "Removed rank from " + playerName);
        player.sendMessage(ChatColor.YELLOW + "Your rank has been removed.");
        return true;
    }

    private boolean handleCheckRank(CommandSender sender, String[] args) {
        String playerName = args.length > 1 ? args[1] : (sender instanceof Player ? sender.getName() : null);
        
        if (playerName == null) {
            sender.sendMessage(ChatColor.RED + "Usage: /rank check [player]");
            return true;
        }

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + playerName);
            return true;
        }

        String rankId = playerRankManager.getPlayerPrimaryRank(player.getUniqueId());
        Rank rank = rankManager.getRank(rankId);
        
        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "No rank data found for " + playerName);
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "=== Rank Info ===");
        sender.sendMessage(ChatColor.GREEN + "Player: " + playerName);
        sender.sendMessage(ChatColor.GREEN + "Rank: " + rank.getDisplayName() + " (" + rankId + ")");
        sender.sendMessage(ChatColor.GREEN + "Prefix: " + rankManager.formatPrefix(rank));

        return true;
    }

    private boolean handleRankInfo(CommandSender sender, String[] args) {
        return handleCheckRank(sender, args);
    }

    private boolean handleCreateRank(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rank.admin.edit-ranks")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /rank create <id> <displayName> [priority]");
            return true;
        }

        String rankId = args[1].toLowerCase();
        String displayName = args[2];
        int priority = args.length > 3 ? Integer.parseInt(args[3]) : 5;

        Rank rank = rankManager.createRank(rankId, displayName, priority);
        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "Rank already exists: " + rankId);
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Created rank: " + rankId);
        return true;
    }

    private boolean handleEditRank(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rank.admin.edit-ranks")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /rank edit <rankId> <property> <value>");
            sender.sendMessage(ChatColor.YELLOW + "Properties: prefix, suffix, price, permission");
            return true;
        }

        String rankId = args[1].toLowerCase();
        String property = args[2].toLowerCase();
        String value = String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length));

        Rank rank = rankManager.getRank(rankId);
        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "Rank not found: " + rankId);
            return true;
        }

        switch (property) {
            case "prefix":
                rank.setPrefix(value);
                sender.sendMessage(ChatColor.GREEN + "Set prefix to: " + value);
                break;
            case "suffix":
                rank.setSuffix(value);
                sender.sendMessage(ChatColor.GREEN + "Set suffix to: " + value);
                break;
            case "price":
                rank.setPurchasePrice(Long.parseLong(value));
                sender.sendMessage(ChatColor.GREEN + "Set price to: " + value);
                break;
            case "permission":
                rank.addPermission(value);
                sender.sendMessage(ChatColor.GREEN + "Added permission: " + value);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown property: " + property);
                return true;
        }

        rankManager.updateRank(rank);
        return true;
    }

    private boolean handleListRanks(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "=== Available Ranks ===");
        rankManager.getRanksSortedByPriority().forEach(rank ->
                sender.sendMessage(ChatColor.GREEN + rank.getId() + " - " + 
                        rank.getDisplayName() + " (Priority: " + rank.getPriority() + ")")
        );
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Rank Commands ===");
        sender.sendMessage(ChatColor.GREEN + "/rank check [player] - Check player rank");
        sender.sendMessage(ChatColor.GREEN + "/rank list - List all ranks");
        
        if (sender.hasPermission("rank.admin.manage-ranks")) {
            sender.sendMessage(ChatColor.YELLOW + "/rank set <player> <rankId> - Assign rank");
            sender.sendMessage(ChatColor.YELLOW + "/rank give <player> <rankId> [hours] - Give temporary rank");
            sender.sendMessage(ChatColor.YELLOW + "/rank remove <player> - Remove rank");
        }
        
        if (sender.hasPermission("rank.admin.edit-ranks")) {
            sender.sendMessage(ChatColor.RED + "/rank create <id> <name> [priority] - Create rank");
            sender.sendMessage(ChatColor.RED + "/rank edit <rankId> <property> <value> - Edit rank");
        }
    }
}
