package com.webx.ranks.gui;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;

/**
 * Меню редактирования префикса/суффикса (выводит текущие значения и команды для изменения).
 */
public class RankPrefixSuffixGUI {
    public void open(Player player, String rankId) {
        Rank rank = Rank.getById(rankId);
        if (rank == null) {
            player.sendMessage("§cРанг не найден: " + rankId);
            return;
        }
        String prefix = rank.getPrefix() != null ? rank.getPrefix() : "§7не задан";
        String suffix = rank.getSuffix() != null ? rank.getSuffix() : "§7не задан";
        player.sendMessage("§6=== Префикс/Суффикс для ранга: " + rank.getDisplayName() + " ===");
        player.sendMessage("§eПрефикс: §f" + prefix + "  Пример: " + prefix + rank.getDisplayName());
        player.sendMessage("§eСуффикс: §f" + suffix + "  Пример: " + rank.getDisplayName() + suffix);
        player.sendMessage("§7Изменить префикс: /setrankprefix " + rank.getId() + " <текст>");
        player.sendMessage("§7Изменить суффикс: /setranksuffix " + rank.getId() + " <текст>");
    }
}
