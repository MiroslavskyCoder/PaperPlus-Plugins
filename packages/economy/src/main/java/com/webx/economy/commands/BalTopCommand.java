package com.webx.economy.commands;

import com.webx.economy.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class BalTopCommand implements CommandExecutor {
    private final EconomyPlugin plugin;

    public BalTopCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        player.sendMessage("§6§l═══════ Top Balances ═══════");

        var topBalances = plugin.getBalTopManager().getTopBalances();
        int rank = 1;
        for (var entry : topBalances) {
            UUID uuid = entry.getKey();
            double balance = entry.getValue();
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            String symbol = plugin.getConfigManager().getCurrencySymbol();

            player.sendMessage(String.format("§e#%d §f%s §7- §6%s%.2f",
                    rank, name, symbol, balance));
            rank++;
        }

        return true;
    }
}
