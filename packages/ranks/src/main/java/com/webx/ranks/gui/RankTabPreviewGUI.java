package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;
import com.webx.ranks.display.TabFormatter;

/**
 * Меню предпросмотра таба (выводит пример отображения игрока в табе).
 */
public class RankTabPreviewGUI {
    public void open(Player player, String rankId) {
        Rank rank = Rank.getById(rankId);
        if (rank == null) {
            player.sendMessage("§cРанг не найден: " + rankId);
            return;
        }
        String preview = TabFormatter.formatTab(rank, player.getName());
        player.sendMessage("§eПример отображения в табе для ранга §b" + rank.getDisplayName() + ":");
            String tab = (rank.getTabColor() != null ? rank.getTabColor() : "") + (rank.getPrefix() != null ? rank.getPrefix() : "") + rank.getDisplayName() + (rank.getSuffix() != null ? rank.getSuffix() : "");
            player.sendMessage("§7[Таб]: " + tab.replace("&", "§"));
            player.sendMessage("§7Изменить таб-цвет: /setranktabcolor <ранг> <код>");
            player.sendMessage("§7Изменить префикс: /setrankprefix <ранг> <текст>");
            player.sendMessage("§7Изменить суффикс: /setranksuffix <ранг> <текст>");
    }
}
