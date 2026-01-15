package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;
import java.util.List;

/**
 * Меню выбора родительской группы (выводит текущего родителя и команды для выбора).
 */
public class RankParentSelectorGUI {
    public void open(Player player, String rankId) {
        Rank rank = Rank.getById(rankId);
        if (rank == null) {
            player.sendMessage("§cРанг не найден: " + rankId);
            return;
        }
        player.sendMessage("§6=== Выбор родителя для: " + rank.getDisplayName() + " ===");
        player.sendMessage("§eТекущий родитель: §f" + (rank.getParent() != null ? rank.getParent() : "§7нет"));
        List<Rank> allRanks = Rank.getAll();
        allRanks.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        player.sendMessage("§7Возможные родители:");
        for (Rank r : allRanks) {
            if (!r.getId().equals(rank.getId())) {
                player.sendMessage(" §b" + r.getDisplayName() + " §8(/setrankparent " + rank.getId() + " " + r.getId() + ")");
            }
        }
        player.sendMessage("§7Удалить родителя: /removerankparent " + rank.getId());
        player.sendMessage("§7Показать дерево: /rankgrouptree");
        player.sendMessage("§7Фильтр по имени: /rankparent <ранг> <фильтр>");
    }
}
