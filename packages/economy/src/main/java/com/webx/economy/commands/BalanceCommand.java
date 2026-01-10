package com.webx.economy.commands;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class BalanceCommand implements CommandExecutor {
    private final EconomyPlugin plugin;

    public BalanceCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            Account account = plugin.getAccountManager().getAccount(player);
            plugin.getMessageManager().send(player, "balance",
                    Map.of(
                            "balance", String.format("%.2f", account.getBalance()),
                            "symbol", plugin.getConfigManager().getCurrencySymbol()
                    ));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageManager().send(player, "player-not-found");
            return true;
        }

        Account account = plugin.getAccountManager().getAccount(target);
        plugin.getMessageManager().send(player, "balance-other",
                Map.of(
                        "player", target.getName(),
                        "balance", String.format("%.2f", account.getBalance()),
                        "symbol", plugin.getConfigManager().getCurrencySymbol()
                ));

        return true;
    }
}
