package com.webx.ranks.gui;

import org.bukkit.entity.Player;

/**
 * Меню поиска по рангам (выводит команды для поиска).
 */
public class RankSearchGUI {
    public void open(Player player, String query, java.util.List<com.webx.ranks.models.Rank> allRanks) {
        player.sendMessage("§6=== Поиск рангов ===");
        java.util.List<com.webx.ranks.models.Rank> found = allRanks.stream()
            .filter(rank -> rank.getName().toLowerCase().contains(query.toLowerCase()) || rank.getId().toLowerCase().contains(query.toLowerCase()))
            .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
            .toList();
        if (found.isEmpty()) {
            player.sendMessage("§cНичего не найдено по запросу: " + query);
            return;
        }
        for (com.webx.ranks.models.Rank rank : found) {
            player.sendMessage("§e" + rank.getName() + " §7(ID: " + rank.getId() + ") §8[/rank info " + rank.getId() + "]");
        }
    }
}
