package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Меню выбора ранга для игрока (отображает список доступных рангов в чате).
 */
public class RankSelectorGUI {
    /**
     * Открыть меню выбора ранга для игрока.
     * @param player игрок
     */
    public void open(Player player) {
        List<Rank> allRanks = Rank.getAll();
        if (allRanks == null || allRanks.isEmpty()) {
            player.sendMessage("§cНет доступных рангов.");
            return;
        }
        allRanks.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
        player.sendMessage("§eВыберите ранг:");
        for (Rank rank : allRanks) {
            player.sendMessage(" §7- §b" + rank.getDisplayName() + " §7(/rank assign " + player.getName() + " " + rank.getId() + ")");
        }
    }
}
