package com.webx.economy.managers;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BankManager {
    private final EconomyPlugin plugin;
    private BukkitRunnable interestTask;

    public BankManager(EconomyPlugin plugin) {
        this.plugin = plugin;
        startInterestTask();
    }

    public boolean depositToBank(Player player, double amount) {
        Account account = plugin.getAccountManager().getAccount(player);

        if (!account.hasBalance(amount)) {
            return false;
        }

        double maxBalance = plugin.getConfig().getDouble("bank.max-balance", 10000000.0);
        if (account.getBankBalance() + amount > maxBalance) {
            return false;
        }

        account.withdraw(amount);
        account.depositBank(amount);
        plugin.getAccountManager().saveAccount(account);

        return true;
    }

    public boolean withdrawFromBank(Player player, double amount) {
        Account account = plugin.getAccountManager().getAccount(player);

        if (!account.hasBankBalance(amount)) {
            return false;
        }

        account.withdrawBank(amount);
        account.deposit(amount);
        plugin.getAccountManager().saveAccount(account);

        return true;
    }

    private void startInterestTask() {
        if (!plugin.getConfig().getBoolean("bank.interest-enabled", true)) {
            return;
        }

        long interval = plugin.getConfig().getLong("bank.interest-interval", 86400) * 20L;

        interestTask = new BukkitRunnable() {
            @Override
            public void run() {
                applyInterest();
            }
        };

        interestTask.runTaskTimer(plugin, interval, interval);
    }

    private void applyInterest() {
        double rate = plugin.getConfig().getDouble("bank.interest-rate", 0.5) / 100.0;
        long now = System.currentTimeMillis();

        plugin.getAccountManager().getTopAccounts(Integer.MAX_VALUE).forEach(account -> {
            if (account.getBankBalance() > 0) {
                double interest = account.getBankBalance() * rate;
                account.depositBank(interest);
                account.setLastInterest(now);
                plugin.getAccountManager().saveAccount(account);
            }
        });
    }

    public void stopInterestTask() {
        if (interestTask != null) {
            interestTask.cancel();
        }
    }
}
