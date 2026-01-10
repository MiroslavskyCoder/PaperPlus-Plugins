package com.webx.economy.storage;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YamlStorage implements StorageProvider {
    private final EconomyPlugin plugin;
    private File accountsFile;
    private FileConfiguration accountsConfig;

    public YamlStorage(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        accountsFile = new File(plugin.getDataFolder(), "accounts.yml");
        if (!accountsFile.exists()) {
            try {
                accountsFile.getParentFile().mkdirs();
                accountsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create accounts.yml: " + e.getMessage());
            }
        }
        accountsConfig = YamlConfiguration.loadConfiguration(accountsFile);
    }

    @Override
    public void close() {
        // Nothing to close for YAML
    }

    @Override
    public List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        
        if (accountsConfig == null) return accounts;

        for (String key : accountsConfig.getKeys(false)) {
            ConfigurationSection section = accountsConfig.getConfigurationSection(key);
            if (section == null) continue;

            try {
                UUID uuid = UUID.fromString(key);
                double balance = section.getDouble("balance", 0.0);
                double bankBalance = section.getDouble("bank-balance", 0.0);
                long lastInterest = section.getLong("last-interest", System.currentTimeMillis());

                Account account = new Account(uuid, balance, bankBalance, lastInterest);
                accounts.add(account);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load account: " + key);
            }
        }

        return accounts;
    }

    @Override
    public void saveAccounts(List<Account> accounts) {
        accountsConfig = new YamlConfiguration();

        for (Account account : accounts) {
            saveAccountToConfig(account);
        }

        try {
            accountsConfig.save(accountsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save accounts.yml: " + e.getMessage());
        }
    }

    @Override
    public void saveAccount(Account account) {
        saveAccountToConfig(account);

        try {
            accountsConfig.save(accountsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save account: " + e.getMessage());
        }
    }

    @Override
    public void deleteAccount(UUID uuid) {
        accountsConfig.set(uuid.toString(), null);

        try {
            accountsConfig.save(accountsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not delete account: " + e.getMessage());
        }
    }

    private void saveAccountToConfig(Account account) {
        String path = account.getOwner().toString();

        accountsConfig.set(path + ".balance", account.getBalance());
        accountsConfig.set(path + ".bank-balance", account.getBankBalance());
        accountsConfig.set(path + ".last-interest", account.getLastInterest());
    }
}
