package com.webx.ranks.gui;

import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import com.webx.ranks.models.Permission;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;

/**
 * Меню управления правами ранга (выводит права и команды для их изменения).
 */
public class RankPermissionToggleGUI {
    public void open(Player player, String rankId) {
        Rank rank = Rank.getById(rankId);
        if (rank == null) {
            player.sendMessage("§cРанг не найден: " + rankId);
            return;
        }
        player.sendMessage("§eПрава ранга §b" + rank.getDisplayName() + ":");
        // Здесь можно вызвать статический show() для CommandSender, если нужно
    }
    // Статический метод для CommandSender
    public static void show(Rank rank, CommandSender sender) {
        sender.sendMessage("§6=== Разрешения ранга: " + rank.getName() + " ===");
        List<Permission> perms = new ArrayList<>(rank.getPermissions());
        perms.sort(Comparator.comparing(Permission::getName));
        for (Permission perm : perms) {
            String status = perm.isEnabled() ? "§aВключено" : "§cВыключено";
            String color = perm.isEnabled() ? "§a" : "§c";
            sender.sendMessage(color + "- " + perm.getName() + ": " + status + " §8[§e/togglerankperm " + rank.getName() + " " + perm.getName() + "§8]");
        }
        sender.sendMessage("§7Используйте: /togglerankperm <ранг> <разрешение> для переключения.");
    }
}
