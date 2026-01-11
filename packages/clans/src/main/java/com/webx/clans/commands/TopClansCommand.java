package com.webx.clans.commands;

import com.webx.clans.ClansPlugin;
import com.webx.clans.managers.ClanManager;
import com.webx.clans.models.Clan;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * Command to display top clans by power
 */
public class TopClansCommand implements CommandExecutor {
    private final ClansPlugin plugin;
    private final ClanManager clanManager;

    public TopClansCommand(ClansPlugin plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int limit = 10;
        
        if (args.length > 0) {
            try {
                limit = Integer.parseInt(args[0]);
                if (limit < 1 || limit > 50) {
                    sender.sendMessage(Component.text("Limit must be between 1 and 50", NamedTextColor.RED));
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid number: " + args[0], NamedTextColor.RED));
                return true;
            }
        }

        // Get top clans sorted by power
        List<Clan> topClans = clanManager.getAllClans().stream()
                .sorted(Comparator.comparingDouble(Clan::getPower).reversed())
                .limit(limit)
                .toList();

        if (topClans.isEmpty()) {
            sender.sendMessage(Component.text("No clans found", NamedTextColor.YELLOW));
            return true;
        }

        // Display header
        sender.sendMessage(Component.text()
                .append(Component.text("═══════════════════", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text(" TOP CLANS BY POWER", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("═══════════════════", NamedTextColor.GOLD, TextDecoration.BOLD))
                .build());

        // Display top clans
        int position = 1;
        for (Clan clan : topClans) {
            OfflinePlayer leader = Bukkit.getOfflinePlayer(clan.getLeader());
            String leaderName = leader.getName() != null ? leader.getName() : "Unknown";

            Component positionComponent = Component.text("#" + position + " ", getMedalColor(position), TextDecoration.BOLD);
            Component tagComponent = Component.text("[" + clan.getTag() + "] ", NamedTextColor.GOLD, TextDecoration.BOLD);
            Component nameComponent = Component.text(clan.getName(), NamedTextColor.WHITE);
            Component statsComponent = Component.text()
                    .append(Component.newline())
                    .append(Component.text("    Power: ", NamedTextColor.GRAY))
                    .append(Component.text(String.format("%.2f", clan.getPower()), NamedTextColor.GOLD))
                    .append(Component.text(" | Members: ", NamedTextColor.GRAY))
                    .append(Component.text(clan.getMemberCount(), NamedTextColor.YELLOW))
                    .append(Component.text(" | Leader: ", NamedTextColor.GRAY))
                    .append(Component.text(leaderName, NamedTextColor.AQUA))
                    .build();

            sender.sendMessage(Component.text()
                    .append(positionComponent)
                    .append(tagComponent)
                    .append(nameComponent)
                    .append(statsComponent)
                    .build());

            position++;
        }

        sender.sendMessage(Component.text("═══════════════════", NamedTextColor.GOLD, TextDecoration.BOLD));

        return true;
    }

    private NamedTextColor getMedalColor(int position) {
        return switch (position) {
            case 1 -> NamedTextColor.GOLD;
            case 2 -> NamedTextColor.GRAY;
            case 3 -> NamedTextColor.RED;
            default -> NamedTextColor.YELLOW;
        };
    }
}
