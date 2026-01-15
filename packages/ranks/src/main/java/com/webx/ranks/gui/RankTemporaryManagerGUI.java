package com.webx.ranks.gui;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

import org.bukkit.entity.Player;
import com.webx.ranks.temp.TemporaryRankManager;
import java.util.Map;

/**
 * Меню управления временными рангами (выводит текущие временные ранги и команды для управления).
 */
public class RankTemporaryManagerGUI {
    public void open(Player player) {
        Map<String, Long> tempRanks = new TemporaryRankManager().getExpiringRanks(player.getUniqueId());
            if (tempRanks == null || tempRanks.isEmpty()) {
                player.sendMessage("§7Нет временных рангов.");
                return;
            }
            player.sendMessage("§6=== Временные ранги игрока ===");
            for (Map.Entry<String, Long> entry : tempRanks.entrySet()) {
                String rankId = entry.getKey();
                long expireAt = entry.getValue();
                player.sendMessage("§eРанг: §b" + rankId + " §7до §f" + expireAt + " §8(/extendranktemp " + rankId + " <минут>)");
                player.sendMessage("§7Продлить: /extendranktemp " + rankId + " <минут>");
                player.sendMessage("§7Удалить: /removeranktemp " + rankId);
            }
    }
}
