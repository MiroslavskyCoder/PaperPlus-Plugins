package com.webx.ranks.commands;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.models.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.ArrayList;

/**
 * Команда для вывода дерева наследования групп: /ranktree
 */
public class RankGroupTreeCommand implements CommandExecutor {
    private final RankManager rankManager;

    public RankGroupTreeCommand(RankManager rankManager) {
        this.rankManager = rankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        List<Rank> allRanks = new ArrayList<>(rankManager.getAllRanks());
        if (allRanks == null || allRanks.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Нет доступных групп.");
            return true;
        }
        player.sendMessage(ChatColor.GOLD + "=== Дерево наследования групп ===");
        allRanks.stream().sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).forEach(rank -> {
            showTree(player, rank, allRanks, 0);
        });
        return true;
    }

    private static void showTree(Player player, Rank rank, List<Rank> allRanks, int level) {
        String indent = "  ".repeat(level);
        player.sendMessage(indent + ChatColor.YELLOW + rank.getDisplayName() + ChatColor.GRAY + " → " + ChatColor.WHITE + (rank.getParent() != null ? rank.getParent() : ChatColor.GRAY + "нет"));
        for (String childId : rank.getChildren()) {
            allRanks.stream().filter(r -> r.getId().equals(childId)).findFirst().ifPresent(child -> showTree(player, child, allRanks, level + 1));
        }
    }
}
