package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import com.webx.ranks.temp.TemporaryRankManager;
import java.util.Map;

/**
 * Меню управления истечением рангов (выводит истекающие ранги и команды для управления).
 */
public class RankExpireManagerGUI {
    public void open(Player player) {
        Map<String, Long> tempRanks = new TemporaryRankManager().getExpiringRanks(player.getUniqueId());
        if (tempRanks == null || tempRanks.isEmpty()) {
            player.sendMessage("§7Нет временных рангов.");
            return;
        }
        player.sendMessage("§eВременные ранги:");
        for (Map.Entry<String, Long> entry : tempRanks.entrySet()) {
            String rankId = entry.getKey();
            long expireAt = entry.getValue();
            player.sendMessage(" §b" + rankId + " §7до §f" + expireAt + " §8(/rank expire " + rankId + " remove)");
        }
        if (tempRanks.size() > 0) {
            for (Map.Entry<String, Long> entry : tempRanks.entrySet()) {
                String rankId = entry.getKey();
                long expireAt = entry.getValue();
                player.sendMessage("§6=== Временный статус для ранга: " + rankId + " ===");
                player.sendMessage("§eВременный: §f" + (new TemporaryRankManager().isTemporary(rankId) ? "§aДа" : "§cНет"));
                player.sendMessage("§eИстекает: §f" + (expireAt != 0 ? expireAt : "§7постоянно"));
                player.sendMessage("§7Установить временный: /setranktemp <ранг> <минут>");
                player.sendMessage("§7Сделать постоянным: /setrankpermanent <ранг>");
                if (new TemporaryRankManager().isTemporary(rankId)) {
                    player.sendMessage("§7Удалить временный статус: /removeranktemp <ранг>");
                }
            }
        }
    }
}
