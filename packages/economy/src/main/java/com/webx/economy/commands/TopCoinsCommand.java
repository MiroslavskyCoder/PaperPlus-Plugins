package com.webx.economy.commands;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.managers.AccountManager;
import com.webx.economy.models.Account;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Command to display top players by coin balance
 */
public class TopCoinsCommand implements CommandExecutor {
    private final EconomyPlugin plugin;
    private final AccountManager accountManager;

    public TopCoinsCommand(EconomyPlugin plugin, AccountManager accountManager) {
        this.plugin = plugin;
        this.accountManager = accountManager;
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

        // Get top accounts
        List<Account> topAccounts = accountManager.getTopAccounts(limit);

        if (topAccounts.isEmpty()) {
            sender.sendMessage(Component.text("No accounts found", NamedTextColor.YELLOW));
            return true;
        }

        // Display header
        sender.sendMessage(Component.text()
                .append(Component.text("═══════════════════", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text(" TOP PLAYERS BY COINS", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("═══════════════════", NamedTextColor.GOLD, TextDecoration.BOLD))
                .build());

        // Display top accounts
        int position = 1;
        for (Account account : topAccounts) {
            UUID uuid = account.getOwner();
            double balance = account.getBalance();
            
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String playerName = player.getName() != null ? player.getName() : uuid.toString().substring(0, 8);

            Component positionComponent = Component.text("#" + position + " ", getMedalColor(position), TextDecoration.BOLD);
            Component nameComponent = Component.text(playerName, NamedTextColor.WHITE);
            Component balanceComponent = Component.text(" - ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(String.format("%.2f", balance), NamedTextColor.GOLD))
                    .append(Component.text(" coins", NamedTextColor.YELLOW));

            sender.sendMessage(Component.text()
                    .append(positionComponent)
                    .append(nameComponent)
                    .append(balanceComponent)
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
