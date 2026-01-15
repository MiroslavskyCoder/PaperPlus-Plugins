package com.webx.economy.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webx.economy.models.Account;
import com.webx.redis.ConfigStandardConfig;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Redis-backed storage for economy accounts with optional root-level config fallback.
 */
public class RedisStorage implements StorageProvider {
    private static final Type ACCOUNT_MAP_TYPE = new TypeToken<Map<String, AccountData>>() {}.getType();

    private final JavaPlugin plugin;
    private final Gson gson;
    private RedisIO redisIO;
    private String keyPrefix;

    public RedisStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void initialize() {
        this.keyPrefix = plugin.getConfig().getString("database.redis.key-prefix", "economy:accounts");
        if (keyPrefix == null || keyPrefix.isBlank()) {
            keyPrefix = "economy:accounts";
        }

        RedisConfig cfg = loadRedisConfig();
        this.redisIO = new RedisIO(cfg, plugin.getLogger());
        if (!redisIO.ping()) {
            plugin.getLogger().warning("Redis ping failed during Economy startup");
        } else {
            plugin.getLogger().info("Connected to Redis at " + cfg.host + ":" + cfg.port + " db=" + cfg.database);
        }
    }

    @Override
    public void close() {
        if (redisIO != null) {
            redisIO.close();
        }
    }

    @Override
    public List<Account> loadAccounts() {
        Map<String, AccountData> raw = loadAccountDataMap();
        return raw.values().stream()
                .map(data -> new Account(data.uuid, data.balance, data.bankBalance, data.lastInterest))
                .toList();
    }

    @Override
    public Account loadAccount(UUID uuid) {
        Map<String, AccountData> raw = loadAccountDataMap();
        AccountData data = raw.get(uuid.toString());
        if (data == null) {
            return null;
        }
        return new Account(data.uuid, data.balance, data.bankBalance, data.lastInterest);
    }

    @Override
    public void saveAccounts(List<Account> accounts) {
        Map<String, AccountData> map = new HashMap<>();
        for (Account account : accounts) {
            map.put(account.getOwner().toString(), new AccountData(account));
        }
        redisIO.setString(allAccountsKey(), gson.toJson(map));
    }

    @Override
    public void saveAccount(Account account) {
        Map<String, AccountData> map = loadAccountDataMap();
        map.put(account.getOwner().toString(), new AccountData(account));
        redisIO.setString(allAccountsKey(), gson.toJson(map));
    }

    @Override
    public void deleteAccount(UUID uuid) {
        Map<String, AccountData> map = loadAccountDataMap();
        map.remove(uuid.toString());
        redisIO.setString(allAccountsKey(), gson.toJson(map));
    }

    private Map<String, AccountData> loadAccountDataMap() {
        Optional<String> json = redisIO.getString(allAccountsKey());
        if (json.isEmpty() || json.get().isBlank()) {
            return new HashMap<>();
        }

        try {
            Map<String, AccountData> map = gson.fromJson(json.get(), ACCOUNT_MAP_TYPE);
            return map != null ? map : new HashMap<>();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to parse Redis economy payload, resetting: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private String allAccountsKey() {
        return keyPrefix + ":all";
    }

    private RedisConfig loadRedisConfig() {
        boolean useRoot = plugin.getConfig().getBoolean("database.redis.use-root-config", true);
        File serverRoot = plugin.getDataFolder().getParentFile() != null
                ? plugin.getDataFolder().getParentFile().getParentFile()
                : null;

        RedisConfig cfg = useRoot
                ? ConfigStandardConfig.load(serverRoot, new RedisConfig(), plugin.getLogger())
                : new RedisConfig();

        cfg.host = plugin.getConfig().getString("database.redis.host", cfg.host);
        cfg.port = plugin.getConfig().getInt("database.redis.port", cfg.port);
        cfg.password = plugin.getConfig().getString("database.redis.password", cfg.password);
        cfg.database = plugin.getConfig().getInt("database.redis.database", cfg.database);
        return cfg;
    }

    private static class AccountData {
        UUID uuid;
        double balance;
        double bankBalance;
        long lastInterest;

        AccountData(Account account) {
            this.uuid = account.getOwner();
            this.balance = account.getBalance();
            this.bankBalance = account.getBankBalance();
            this.lastInterest = account.getLastInterest();
        }
    }
}