package com.webx.economy.commands;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class BankCommand implements CommandExecutor {
    private final EconomyPlugin plugin;

    public BankCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (!plugin.getConfig().getBoolean("bank.enabled", true)) {
            player.sendMessage("§cBank system is disabled!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /bank <deposit|withdraw|balance> [amount]");
            return true;
        }

        String action = args[0].toLowerCase();
        String symbol = plugin.getConfigManager().getCurrencySymbol();

        switch (action) {
            case "balance", "bal" -> {
                Account account = plugin.getAccountManager().getAccount(player);
                plugin.getMessageManager().send(player, "bank-balance",
                        Map.of("balance", String.format("%.2f", account.getBankBalance()),
                                "symbol", symbol));
            }
            case "deposit", "dep" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /bank deposit <amount>");
                    return true;
                }
                double amount = parseAmount(args[1]);
                if (amount <= 0) {
                    plugin.getMessageManager().send(player, "invalid-amount");
                    return true;
                }
                if (plugin.getBankManager().depositToBank(player, amount)) {
                    plugin.getMessageManager().send(player, "bank-deposit",
                            Map.of("amount", String.format("%.2f", amount),
                                    "symbol", symbol));
                } else {
                    plugin.getMessageManager().send(player, "insufficient-funds");
                }
            }
            case "withdraw", "wd" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /bank withdraw <amount>");
                    return true;
                }
                double amount = parseAmount(args[1]);
                if (amount <= 0) {
                    plugin.getMessageManager().send(player, "invalid-amount");
                    return true;
                }
                if (plugin.getBankManager().withdrawFromBank(player, amount)) {
                    plugin.getMessageManager().send(player, "bank-withdraw",
                            Map.of("amount", String.format("%.2f", amount),
                                    "symbol", symbol));
                } else {
                    player.sendMessage("§cInsufficient bank balance!");
                }
            }
            default -> player.sendMessage("§cUsage: /bank <deposit|withdraw|balance> [amount]");
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
