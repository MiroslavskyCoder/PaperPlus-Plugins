package com.webx.economy.commands;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class EcoCommand implements CommandExecutor {
    private final EconomyPlugin plugin;

    public EcoCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (!player.hasPermission("economy.admin")) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage("§cUsage: /eco <give|take|set|reset> <player> <amount>");
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            plugin.getMessageManager().send(player, "player-not-found");
            return true;
        }

        Account account = plugin.getAccountManager().getAccount(target);
        String symbol = plugin.getConfigManager().getCurrencySymbol();

        switch (action) {
            case "give" -> {
                double amount = parseAmount(args[2]);
                if (amount <= 0) {
                    plugin.getMessageManager().send(player, "invalid-amount");
                    return true;
                }
                account.deposit(amount);
                plugin.getAccountManager().saveAccount(account);
                plugin.getMessageManager().send(player, "eco-give",
                        Map.of("amount", String.format("%.2f", amount),
                                "player", target.getName(),
                                "symbol", symbol));
            }
            case "take" -> {
                double amount = parseAmount(args[2]);
                if (amount <= 0) {
                    plugin.getMessageManager().send(player, "invalid-amount");
                    return true;
                }
                account.withdraw(amount);
                plugin.getAccountManager().saveAccount(account);
                plugin.getMessageManager().send(player, "eco-take",
                        Map.of("amount", String.format("%.2f", amount),
                                "player", target.getName(),
                                "symbol", symbol));
            }
            case "set" -> {
                double amount = parseAmount(args[2]);
                if (amount < 0) {
                    plugin.getMessageManager().send(player, "invalid-amount");
                    return true;
                }
                account.setBalance(amount);
                plugin.getAccountManager().saveAccount(account);
                plugin.getMessageManager().send(player, "eco-set",
                        Map.of("amount", String.format("%.2f", amount),
                                "player", target.getName(),
                                "symbol", symbol));
            }
            case "reset" -> {
                plugin.getAccountManager().resetAccount(target.getUniqueId());
                plugin.getMessageManager().send(player, "eco-reset",
                        Map.of("player", target.getName()));
            }
            default -> player.sendMessage("§cUsage: /eco <give|take|set|reset> <player> <amount>");
        }

        return true;
    }

    private double parseAmount(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
