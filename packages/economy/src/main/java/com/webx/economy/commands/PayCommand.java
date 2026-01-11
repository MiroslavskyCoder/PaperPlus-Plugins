package com.webx.economy.commands;

import com.webx.economy.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class PayCommand implements CommandExecutor {
    private final EconomyPlugin plugin;

    public PayCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /pay <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageManager().send(player, "player-not-found");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cYou can't pay yourself!");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            plugin.getMessageManager().send(player, "invalid-amount");
            return true;
        }

        if (!plugin.getTransactionManager().isValidAmount(amount)) {
            plugin.getMessageManager().send(player, "invalid-amount");
            return true;
        }

        if (!plugin.getTransactionManager().transfer(player, target, amount)) {
            plugin.getMessageManager().send(player, "insufficient-funds");
            return true;
        }

        double fee = plugin.getTransactionManager().calculateFee(player, amount);
        String symbol = plugin.getConfigManager().getCurrencySymbol();

        plugin.getMessageManager().send(player, "pay-sent",
                Map.of("amount", String.format("%.2f", amount),
                        "player", target.getName(),
                        "symbol", symbol));

        if (fee > 0) {
            plugin.getMessageManager().send(player, "pay-fee",
                    Map.of("fee", String.format("%.2f", fee),
                            "symbol", symbol));
        }

        plugin.getMessageManager().send(target, "pay-received",
                Map.of("amount", String.format("%.2f", amount),
                        "player", player.getName(),
                        "symbol", symbol));

        return true;
    }
}
