package com.webx.ranks.gui;

import org.bukkit.command.CommandSender;
import java.util.List;
import com.webx.ranks.models.AuditLogEntry;

import org.bukkit.entity.Player;
import java.util.List;

/**
 * Меню аудита изменений рангов (выводит последние действия).
 */
public class RankAuditLogGUI {
    public void open(Player player) {
        List<String> log = getAuditLog();
        if (log == null || log.isEmpty()) {
            player.sendMessage("§7Нет записей аудита.");
            return;
        }
        player.sendMessage("§eПоследние действия с рангами:");
        for (String entry : log) {
            player.sendMessage(" §7- " + entry);
        }
    }
    public static void show(CommandSender sender, List<AuditLogEntry> entries) {
        sender.sendMessage("§6=== Журнал действий с рангами ===");
        entries.stream()
            .sorted((a, b) -> Long.compare(
                b.getTimestamp() != null ? b.getTimestamp().getTime() : 0L,
                a.getTimestamp() != null ? a.getTimestamp().getTime() : 0L))
            .limit(20)
            .forEach(entry -> sender.sendMessage("§7[" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                entry.getTimestamp() != null ? entry.getTimestamp() : new java.util.Date(0)) + "] §e" + entry.getAction() + " §7- §f" + entry.getActor() + " §8(" + entry.getTarget() + ")"));
        sender.sendMessage("§7Показаны последние 20 событий. Фильтр: /rankaudit <игрок|ранг>");
    }

    private List<String> getAuditLog() {
        // TODO: Реализовать получение аудита
        return java.util.Collections.emptyList();
    }
}
