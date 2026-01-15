package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;
import java.util.List;

/**
 * Меню управления группами/наследованием (выводит дерево наследования и команды для изменения parent).
 */
public class RankGroupManagerGUI {
    public void open(Player player) {
        List<Rank> allRanks = Rank.getAll();
        if (allRanks == null || allRanks.isEmpty()) {
            player.sendMessage("§cНет доступных групп.");
            return;
        }
        allRanks.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        player.sendMessage("§6=== Управление группами/наследованием ===");
        for (Rank rank : allRanks) {
            String parent = rank.getParent() != null ? rank.getParent() : "§7нет";
            List<String> children = rank.getChildren();
            String childrenStr = (children != null && !children.isEmpty()) ? String.join(", ", children) : "§7нет";
            player.sendMessage("§eГруппа: §b" + rank.getDisplayName());
            player.sendMessage("  §eРодитель: §f" + parent + " §8(/setrankparent " + rank.getId() + " <parentId>)");
            player.sendMessage("  §eДочерние: §f" + childrenStr + " §8(/addrankchild " + rank.getId() + " <childId>)");
            player.sendMessage("  §7Удалить родителя: /removerankparent " + rank.getId());
            player.sendMessage("  §7Удалить потомка: /removerankchild " + rank.getId() + " <childId>");
        }
        player.sendMessage("§7Используйте команды выше для управления наследованием.");
    }
}
