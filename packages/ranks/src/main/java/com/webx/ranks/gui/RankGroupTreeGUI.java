package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;
import java.util.List;

/**
 * Меню дерева наследования групп (выводит иерархию в чат).
 */
public class RankGroupTreeGUI {
    public void open(Player player) {
        List<Rank> allRanks = Rank.getAll();
        if (allRanks == null || allRanks.isEmpty()) {
            player.sendMessage("§cНет доступных групп.");
            return;
        }
        player.sendMessage("§6=== Дерево наследования групп ===");
        allRanks.stream().sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).forEach(rank -> {
            showTree(player, rank, allRanks, 0);
        });
    }

    private static void showTree(Player player, Rank rank, List<Rank> allRanks, int level) {
        String indent = "  ".repeat(level);
        player.sendMessage(indent + "§e" + rank.getDisplayName() + " §7→ §f" + (rank.getParent() != null ? rank.getParent() : "§7нет"));
        for (String childId : rank.getChildren()) {
            allRanks.stream().filter(r -> r.getId().equals(childId)).findFirst().ifPresent(child -> showTree(player, child, allRanks, level + 1));
        }
    }
}
