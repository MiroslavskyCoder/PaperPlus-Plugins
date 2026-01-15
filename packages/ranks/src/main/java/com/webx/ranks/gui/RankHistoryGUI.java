package com.webx.ranks.gui;

import org.bukkit.command.CommandSender;
import java.util.List;
import com.webx.ranks.models.RankHistoryEntry;

import org.bukkit.entity.Player;
import com.webx.ranks.history.RankHistoryManager;
import com.webx.ranks.history.RankHistory;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Меню истории рангов игрока (выводит историю в чат).
 */
public class RankHistoryGUI {
    public void open(Player player) {
        List<RankHistory> history = new RankHistoryManager().getHistory(player.getUniqueId());
        if (history == null || history.isEmpty()) {
            player.sendMessage("§7История рангов пуста.");
            return;
        }
        player.sendMessage("§eИстория рангов:");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (RankHistory rh : history) {
            String assigned = rh.getAssignedAt() != null ? sdf.format(rh.getAssignedAt()) : "?";
            String removed = rh.getRemovedAt() != null ? sdf.format(rh.getRemovedAt()) : "активен";
            player.sendMessage(" §7- §b" + rh.getRankId() + " §7c " + assigned + " по " + removed);
        }
    }
    public static void show(CommandSender sender, List<RankHistoryEntry> entries) {
        sender.sendMessage("§6=== История изменений ранга ===");
        entries.stream()
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .limit(20)
            .forEach(entry -> sender.sendMessage("§7[" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(entry.getTimestamp())) + "] §e" + entry.getAction() + " §7- §f" + entry.getActor()));
        sender.sendMessage("§7Показаны последние 20 изменений. Фильтр: /rankhistory <игрок|ранг>");
    }
}
