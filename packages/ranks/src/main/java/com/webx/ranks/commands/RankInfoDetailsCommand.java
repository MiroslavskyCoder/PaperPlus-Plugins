package com.webx.ranks.commands;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.models.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Команда подробной информации о ранге: /rankdetails <rankId>
 */
public class RankInfoDetailsCommand implements CommandExecutor {
    private final RankManager rankManager;

    public RankInfoDetailsCommand(RankManager rankManager) {
        this.rankManager = rankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Использование: /rankdetails <rankId>");
            return true;
        }
        Player player = (Player) sender;
        String rankId = args[0];
        Rank rank = rankManager.getRank(rankId);
        if (rank == null) {
            player.sendMessage(ChatColor.RED + "Ранг не найден: " + rankId);
            return true;
        }
        player.sendMessage(ChatColor.GOLD + "Информация о ранге: " + ChatColor.AQUA + rank.getDisplayName());
        player.sendMessage(ChatColor.GRAY + "ID: " + ChatColor.WHITE + rank.getId());
        player.sendMessage(ChatColor.GRAY + "Приоритет: " + ChatColor.WHITE + rank.getPriority());
        player.sendMessage(ChatColor.GRAY + "Цвет: " + ChatColor.WHITE + rank.getColor());
        player.sendMessage(ChatColor.GRAY + "ChatColor: " + ChatColor.WHITE + (rank.getChatColor() != null ? rank.getChatColor() : ChatColor.GRAY + "не задан"));
        player.sendMessage(ChatColor.GRAY + "TabColor: " + ChatColor.WHITE + (rank.getTabColor() != null ? rank.getTabColor() : ChatColor.GRAY + "не задан"));
        player.sendMessage(ChatColor.GRAY + "Префикс: " + ChatColor.WHITE + rank.getPrefix());
        player.sendMessage(ChatColor.GRAY + "Суффикс: " + ChatColor.WHITE + rank.getSuffix());
        player.sendMessage(ChatColor.GRAY + "Родитель: " + ChatColor.WHITE + (rank.getParent() != null ? rank.getParent() : "-") );
        player.sendMessage(ChatColor.GRAY + "Временный: " + (rank.isTemporary() ? ChatColor.RED + "Да" : ChatColor.GREEN + "Нет"));
        if (rank.isTemporary()) {
            player.sendMessage(ChatColor.GRAY + "Истекает: " + ChatColor.WHITE + (rank.getExpireAt() > 0 ? new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(rank.getExpireAt())) : "никогда"));
        }
        player.sendMessage(ChatColor.GRAY + "Права: " + ChatColor.WHITE + String.join(", ", rank.getPermissions()));
        return true;
    }
}
