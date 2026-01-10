package com.webx.economy.managers;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import com.webx.economy.storage.StorageProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class AccountManager {
    private final EconomyPlugin plugin;
    private final StorageProvider storage;
    private final Map<UUID, Account> accounts;

    public AccountManager(EconomyPlugin plugin, StorageProvider storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.accounts = new HashMap<>();
    }

    public void loadAccounts() {
        accounts.clear();
        List<Account> loaded = storage.loadAccounts();
        for (Account account : loaded) {
            accounts.put(account.getOwner(), account);
        }
        plugin.getLogger().info("Loaded " + accounts.size() + " accounts");
    }

    public void saveAccounts() {
        storage.saveAccounts(new ArrayList<>(accounts.values()));
        plugin.getLogger().info("Saved " + accounts.size() + " accounts");
    }

    public Account getAccount(UUID uuid) {
        return accounts.computeIfAbsent(uuid, k -> createAccount(uuid));
    }

    public Account getAccount(Player player) {
        return getAccount(player.getUniqueId());
    }

    private Account createAccount(UUID uuid) {
        double startingBalance = plugin.getConfig().getDouble("currency.starting-balance", 1000.0);
        Account account = new Account(uuid);
        account.setBalance(startingBalance);
        accounts.put(uuid, account);
        storage.saveAccount(account);
        return account;
    }

    public void saveAccount(Account account) {
        accounts.put(account.getOwner(), account);
        storage.saveAccount(account);
    }

    public boolean exists(UUID uuid) {
        return accounts.containsKey(uuid);
    }

    public void resetAccount(UUID uuid) {
        Account account = getAccount(uuid);
        account.setBalance(plugin.getConfig().getDouble("currency.starting-balance", 1000.0));
        account.setBankBalance(0);
        saveAccount(account);
    }

    public List<Account> getTopAccounts(int limit) {
        return accounts.values().stream()
                .sorted((a, b) -> Double.compare(b.getTotalBalance(), a.getTotalBalance()))
                .limit(limit)
                .toList();
    }

    public int getAccountCount() {
        return accounts.size();
    }
}
