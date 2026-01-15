package com.webx.ranks.gui;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * Меню массового назначения рангов (выводит команды для массового назначения).
 */
public class RankBulkAssignGUI {
    public void open(Player player, String rankId, java.util.List<String> playerNames) {
        player.sendMessage("§6=== Массовая выдача ранга: " + rankId + " ===");
        List<String> online = playerNames.stream()
            .filter(name -> org.bukkit.Bukkit.getPlayerExact(name) != null)
            .sorted(String::compareToIgnoreCase)
            .toList();
        player.sendMessage("§7Онлайн игроки:");
        for (String name : online) {
            player.sendMessage(" §b" + name + " §8(/assignrank " + name + " " + rankId + ")");
        }
        player.sendMessage("§7Массово: /assignrankall " + rankId);
        player.sendMessage("§7Снять у всех: /removerankall " + rankId);
    }
}
