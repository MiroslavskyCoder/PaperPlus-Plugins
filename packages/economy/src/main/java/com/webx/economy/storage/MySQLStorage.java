package com.webx.economy.storage;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLStorage implements StorageProvider {
    private final EconomyPlugin plugin;

    public MySQLStorage(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        // TODO: Implement MySQL connection
        plugin.getLogger().warning("MySQL storage not yet implemented, falling back to YAML");
    }

    @Override
    public void close() {
        // TODO: Close MySQL connection
    }

    @Override
    public List<Account> loadAccounts() {
        // TODO: Load from MySQL
        return new ArrayList<>();
    }

    @Override
    public void saveAccounts(List<Account> accounts) {
        // TODO: Save to MySQL
    }

    @Override
    public void saveAccount(Account account) {
        // TODO: Save single account to MySQL
    }

    @Override
    public void deleteAccount(UUID uuid) {
        // TODO: Delete from MySQL
    }
}
