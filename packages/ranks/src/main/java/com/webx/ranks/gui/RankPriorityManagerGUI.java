package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;
import java.util.List;

/**
 * Меню управления приоритетами рангов (выводит приоритеты и команды для изменения).
 */
public class RankPriorityManagerGUI {
    public void open(Player player) {
        List<Rank> allRanks = Rank.getAll();
        if (allRanks == null || allRanks.isEmpty()) {
            player.sendMessage("§cНет доступных рангов.");
            return;
        }
        player.sendMessage("§eПриоритеты рангов:");
        allRanks.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
        for (Rank rank : allRanks) {
            player.sendMessage(" §b" + rank.getDisplayName() + " §7: §f" + rank.getPriority() + " §8(/setrankpriority " + rank.getName() + " <число>)");
        }
        player.sendMessage("§7Изменить приоритет: /setrankpriority <ранг> <число>");
    }
}
