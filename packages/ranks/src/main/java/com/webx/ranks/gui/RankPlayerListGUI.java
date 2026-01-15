package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import com.webx.ranks.api.RankAPI;
import java.util.UUID;

/**
 * Меню списка игроков с выбранным рангом (выводит список в чат).
 */
public class RankPlayerListGUI {
    public void open(Player player, String rankId) {
        player.sendMessage("§eИгроки с рангом §b" + rankId + ":");
        List<Player> filtered = new java.util.ArrayList<>();
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            if (RankAPI.getPlayerRank(p.getUniqueId()) != null && RankAPI.getPlayerRank(p.getUniqueId()).getId().equals(rankId)) {
                filtered.add(p);
            }
        }
        filtered.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        if (filtered.isEmpty()) {
            player.sendMessage("§7Нет игроков с этим рангом онлайн.");
            return;
        }
        for (Player p : filtered) {
            player.sendMessage(" §7- §f" + p.getName() + " §8/assignrank " + p.getName() + " <ранг>  /removerank " + p.getName());
        }
        player.sendMessage("§7Массово: /assignrankall " + rankId);
        player.sendMessage("§7Снять у всех: /removerankall " + rankId);
    }
}
