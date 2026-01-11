package com.webx.economy.managers;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import com.webx.economy.models.Transaction;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TransactionManager {
    private final EconomyPlugin plugin;

    public TransactionManager(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean transfer(Player from, Player to, double amount) {
        Account fromAccount = plugin.getAccountManager().getAccount(from);
        Account toAccount = plugin.getAccountManager().getAccount(to);

        if (!fromAccount.hasBalance(amount)) {
            return false;
        }

        double fee = calculateFee(from, amount);
        double totalCost = amount + fee;

        if (!fromAccount.hasBalance(totalCost)) {
            return false;
        }

        fromAccount.withdraw(totalCost);
        toAccount.deposit(amount);

        plugin.getAccountManager().saveAccount(fromAccount);
        plugin.getAccountManager().saveAccount(toAccount);

        logTransaction(from.getUniqueId(), to.getUniqueId(), amount, fee, Transaction.TransactionType.PAYMENT);

        return true;
    }

    public double calculateFee(Player player, double amount) {
        if (!plugin.getConfig().getBoolean("transactions.fee-enabled", true)) {
            return 0.0;
        }

        if (player.hasPermission("economy.bypass.transaction-fee")) {
            return 0.0;
        }

        double feePercentage = plugin.getConfig().getDouble("transactions.fee-percentage", 2.0);
        return (amount * feePercentage) / 100.0;
    }

    public boolean isValidAmount(double amount) {
        double min = plugin.getConfig().getDouble("transactions.min-amount", 0.01);
        double max = plugin.getConfig().getDouble("transactions.max-amount", 1000000.0);
        return amount >= min && amount <= max;
    }

    private void logTransaction(UUID from, UUID to, double amount, double fee, Transaction.TransactionType type) {
        Transaction transaction = new Transaction(from, to, amount, fee, type);
        // TODO: Save to database for history
    }
}
