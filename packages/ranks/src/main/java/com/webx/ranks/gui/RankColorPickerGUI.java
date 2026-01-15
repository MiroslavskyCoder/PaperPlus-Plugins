package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;

/**
 * Меню выбора цвета ранга (выводит текущий цвет и команды для смены).
 */
public class RankColorPickerGUI {
    public void open(Player player, String rankId) {
        Rank rank = Rank.getById(rankId);
        if (rank == null) {
            player.sendMessage("§cРанг не найден: " + rankId);
            return;
        }
        String current = rank.getColor() != null ? rank.getColor() : "§7не задан";
        player.sendMessage("§7Текущий цвет: " + current + "  Пример: " + current + rank.getDisplayName());
        String[] codes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"};
        StringBuilder sb = new StringBuilder("§7Доступные цвета: ");
        for (String code : codes) {
            sb.append(code.replace("&", "§")).append(code).append("  ");
        }
        player.sendMessage(sb.toString());
        player.sendMessage("§7Изменить цвет: /rank color " + rank.getId() + " <код>");
        player.sendMessage("§7Пример: /rank color " + rank.getId() + " &a");
    }
}
