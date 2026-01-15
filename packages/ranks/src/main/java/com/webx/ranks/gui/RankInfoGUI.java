package com.webx.ranks.gui;

import org.bukkit.entity.Player;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;

/**
 * Меню информации о ранге (выводит подробности в чат).
 */
public class RankInfoGUI {
    /**
     * Открыть меню информации о ранге.
     * @param player игрок
     * @param rankId id ранга
     */
    public void open(Player player, String rankId) {
        Rank rank = Rank.getById(rankId);
        if (rank == null) {
            player.sendMessage("§cРанг не найден: " + rankId);
            return;
        }
        player.sendMessage("§eИнформация о ранге: §b" + rank.getDisplayName());
        player.sendMessage(" §7ID: §f" + rank.getId());
        player.sendMessage(" §7Приоритет: §f" + rank.getPriority());
        player.sendMessage(" §7Цвет: §f" + rank.getColor());
        player.sendMessage(" §7ChatColor: §f" + (rank.getChatColor() != null ? rank.getChatColor() : "§7не задан"));
        player.sendMessage(" §7TabColor: §f" + (rank.getTabColor() != null ? rank.getTabColor() : "§7не задан"));
        player.sendMessage(" §7Префикс: §f" + rank.getPrefix());
        player.sendMessage(" §7Суффикс: §f" + rank.getSuffix());
        player.sendMessage(" §7Родитель: §f" + (rank.getParent() != null ? rank.getParent() : "-"));
        player.sendMessage(" §7Временный: §f" + (rank.isTemporary() ? "§cДа" : "§aНет"));
        if (rank.isTemporary()) {
            player.sendMessage(" §7Истекает: §f" + (rank.getExpireAt() > 0 ? new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(rank.getExpireAt())) : "никогда"));
        }
        player.sendMessage(" §7Права: §f" + String.join(", ", rank.getPermissions()));
    }
}
