package com.webx.ranks.commands;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.models.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда управления группами/наследованием: /rankgroups
 */
public class RankGroupManagerCommand implements CommandExecutor {
    private final RankManager rankManager;

    public RankGroupManagerCommand(RankManager rankManager) {
        this.rankManager = rankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        List<Rank> allRanks = new ArrayList<>(rankManager.getAllRanks());
        if (allRanks == null || allRanks.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Нет доступных групп.");
            return true;
        }
        allRanks.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        player.sendMessage(ChatColor.GOLD + "=== Управление группами/наследованием ===");
        for (Rank rank : allRanks) {
            String parent = rank.getParent() != null ? rank.getParent() : ChatColor.GRAY + "нет";
            List<String> children = rank.getChildren();
            String childrenStr = (children != null && !children.isEmpty()) ? String.join(", ", children) : ChatColor.GRAY + "нет";
            player.sendMessage(ChatColor.YELLOW + "Группа: " + ChatColor.AQUA + rank.getDisplayName());
            player.sendMessage("  " + ChatColor.YELLOW + "Родитель: " + ChatColor.WHITE + parent + ChatColor.DARK_GRAY + " (/setrankparent " + rank.getId() + " <parentId>)");
            player.sendMessage("  " + ChatColor.YELLOW + "Дочерние: " + ChatColor.WHITE + childrenStr + ChatColor.DARK_GRAY + " (/addrankchild " + rank.getId() + " <childId>)");
            player.sendMessage("  " + ChatColor.GRAY + "Удалить родителя: /removerankparent " + rank.getId());
            player.sendMessage("  " + ChatColor.GRAY + "Удалить потомка: /removerankchild " + rank.getId() + " <childId>");
        }
        player.sendMessage(ChatColor.GRAY + "Используйте команды выше для управления наследованием.");
        return true;
    }
}
