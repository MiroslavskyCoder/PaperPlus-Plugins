package com.webx.economy.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.economy.models.Account;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * JSON-based storage provider for player economic data
 * Stores each player's account in a separate JSON file
 */
public class JsonStorage implements StorageProvider {
    private final Gson gson;
    private final Path dataFolder;
    
    public JsonStorage(File pluginDataFolder) {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.dataFolder = pluginDataFolder.toPath().resolve("playerdata");
        
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create JSON storage directory", e);
        }
    }
    
    @Override
    public void initialize() {
        // Initialization already done in constructor
    }
    
    @Override
    public void close() {
        // No resources to close
    }
    
    @Override
    public void saveAccount(Account account) {
        Path filePath = getAccountFile(account.getOwner());
        
        try (Writer writer = new FileWriter(filePath.toFile())) {
            gson.toJson(new AccountData(account), writer);
        } catch (IOException e) {
            System.err.println("Failed to save account " + account.getOwner() + ": " + e.getMessage());
        }
    }
    
    @Override
    public void saveAccounts(List<Account> accounts) {
        for (Account account : accounts) {
            saveAccount(account);
        }
    }
    
    @Override
    public List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        
        File[] files = dataFolder.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return accounts;
        
        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                AccountData data = gson.fromJson(reader, AccountData.class);
                if (data != null) {
                    Account account = new Account(data.uuid);
                    account.setBalance(data.balance);
                    account.setBankBalance(data.bankBalance);
                    accounts.add(account);
                }
            } catch (IOException e) {
                System.err.println("Failed to load account from " + file.getName() + ": " + e.getMessage());
            }
        }
        
        return accounts;
    }
    
    @Override
    public Account loadAccount(UUID uuid) {
        Path filePath = getAccountFile(uuid);
        
        if (!Files.exists(filePath)) {
            return null;
        }
        
        try (Reader reader = new FileReader(filePath.toFile())) {
            AccountData data = gson.fromJson(reader, AccountData.class);
            if (data != null) {
                Account account = new Account(data.uuid);
                account.setBalance(data.balance);
                account.setBankBalance(data.bankBalance);
                return account;
            }
        } catch (IOException e) {
            System.err.println("Failed to load account " + uuid + ": " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public void deleteAccount(UUID uuid) {
        Path filePath = getAccountFile(uuid);
        
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Failed to delete account " + uuid + ": " + e.getMessage());
        }
    }
    
    private Path getAccountFile(UUID uuid) {
        return dataFolder.resolve(uuid.toString() + ".json");
    }
    
    /**
     * Data class for JSON serialization
     */
    private static class AccountData {
        UUID uuid;
        double balance;
        double bankBalance;
        long lastSeen;
        
        AccountData(Account account) {
            this.uuid = account.getOwner();
            this.balance = account.getBalance();
            this.bankBalance = account.getBankBalance();
            this.lastSeen = System.currentTimeMillis();
        }
    }
}
