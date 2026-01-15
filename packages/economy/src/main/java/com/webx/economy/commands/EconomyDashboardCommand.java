package com.webx.economy.commands;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import com.webx.unigui.GuiService;
import com.webx.unigui.Slots;
import com.webx.unigui.Theme;
import com.webx.unigui.ThemedView;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicReference;

public class EconomyDashboardCommand implements CommandExecutor {
    private final EconomyPlugin plugin;
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");

    public EconomyDashboardCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can open the economy dashboard.");
            return true;
        }

        open(player);
        return true;
    }

    private void open(Player player) {
        GuiService gui = plugin.getGuiService();
        Account account = plugin.getAccountManager().getAccount(player);

        double bankCap = plugin.getConfig().getDouble("bank.max-balance", 10_000_000.0);
        double total = account.getTotalBalance();
        double ratio = bankCap <= 0 ? 0 : Math.min(1.0, total / bankCap);

        AtomicReference<Double> amountRef = new AtomicReference<>(Math.min(10_000, bankCap));

        ThemedView view = new ThemedView("Economy", 5, Theme.neon());
        view.addField(Slots.at(0, 1), "Balance", moneyFormat.format(account.getBalance()));
        view.addField(Slots.at(0, 4), "Bank", moneyFormat.format(account.getBankBalance()));
        view.addProgress(Slots.at(0, 7), "Total", ratio, "Cap: " + moneyFormat.format(bankCap));

        view.addSlider(Slots.at(2, 4), "Amount", amountRef.get(), 0.0, bankCap, 250.0, amountRef::set);

        view.addButton(Slots.at(3, 3), "Deposit → Bank", ctx -> {
            double value = amountRef.get();
            boolean ok = plugin.getBankManager().depositToBank(player, value);
            if (ok) {
                player.sendMessage(ChatColor.GREEN + "Deposited " + moneyFormat.format(value) + " to bank.");
            } else {
                player.sendMessage(ChatColor.RED + "Deposit failed (insufficient balance or bank cap reached).");
            }
            open(ctx.player());
        });

        view.addButton(Slots.at(3, 5), "Withdraw ← Bank", ctx -> {
            double value = amountRef.get();
            boolean ok = plugin.getBankManager().withdrawFromBank(player, value);
            if (ok) {
                player.sendMessage(ChatColor.GREEN + "Withdrew " + moneyFormat.format(value) + " from bank.");
            } else {
                player.sendMessage(ChatColor.RED + "Withdraw failed (insufficient bank balance).");
            }
            open(ctx.player());
        });

        view.addButton(Slots.at(4, 4), "Refresh", ctx -> open(ctx.player()));

        gui.open(player, view);
    }
}
