package com.webx.ranks.gui;

import org.bukkit.entity.Player;

/**
 * Меню экспорта/импорта конфигов рангов (выводит команды для экспорта/импорта).
 */
public class RankExportImportGUI {
    public void open(Player player) {
        player.sendMessage("§eЭкспорт/импорт конфигов рангов:");
           player.sendMessage(" §7Экспортировать все ранги в JSON: /rank export <файл>");
           player.sendMessage(" §7Импортировать из JSON: /rank import <файл>");
           player.sendMessage(" §7Пример экспорта: /rank export backup_2026-01-15.json");
           player.sendMessage(" §7Пример импорта: /rank import backup_2026-01-15.json");
           player.sendMessage(" §7Текущий статус: " + (com.webx.ranks.managers.RankManager.isLastImportSuccess() ? "§aИмпорт успешен" : "§cОшибки при импорте"));
    }
}
