package com.webx.api.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webx.redis.ConfigStandardConfig;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;
import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST API service for Leaderboards using reflection
 */
public class LeaderboardService {
    private final Gson gson;
    private final JavaPlugin plugin;
    private RedisIO redisIO;

    private static final String ECONOMY_ACCOUNTS_KEY = "economy:accounts:all";
    private static final String CLANS_KEY = "clans:data";

    public LeaderboardService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * GET /api/leaderboards/players - Get top players by coins
     */
    public void getTopPlayers(Context ctx) {
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);
        if (limit < 1 || limit > 100) {
            limit = 10;
        }

        Object accountManager = getAccountManager();
        if (accountManager == null) {
            List<Map<String, Object>> fallback = getTopPlayersFromRedis(limit);
            if (fallback.isEmpty()) {
                ctx.status(503).json(Map.of("error", "Economy plugin not available"));
                return;
            }
            ctx.json(Map.of("leaderboard", fallback, "total", fallback.size()));
            return;
        }

        try {
            Method getTopAccountsMethod = accountManager.getClass().getMethod("getTopAccounts", int.class);
            List<?> topAccounts = (List<?>) getTopAccountsMethod.invoke(accountManager, limit);
            List<Map<String, Object>> leaderboard = new ArrayList<>();

            int position = 1;
            for (Object account : topAccounts) {
                // Account object with getOwner() and getBalance() methods
                Method getOwnerMethod = account.getClass().getMethod("getOwner");
                Method getBalanceMethod = account.getClass().getMethod("getBalance");
                
                UUID uuid = (UUID) getOwnerMethod.invoke(account);
                double balance = (double) getBalanceMethod.invoke(account);
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                Map<String, Object> playerData = new HashMap<>();
                playerData.put("position", position++);
                playerData.put("uuid", uuid.toString());
                playerData.put("name", player.getName() != null ? player.getName() : "Unknown");
                playerData.put("balance", balance);
                playerData.put("online", player.isOnline());

                leaderboard.add(playerData);
            }

            ctx.json(Map.of(
                    "leaderboard", leaderboard,
                    "total", leaderboard.size()
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to get leaderboard: " + e.getMessage()));
        }
    }

    /**
     * GET /api/leaderboards/stats - Get combined stats
     */
    public void getCombinedStats(Context ctx) {
        Map<String, Object> stats = new HashMap<>();

        // Get player count
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int totalPlayers = Bukkit.getOfflinePlayers().length;
        
        stats.put("players", Map.of(
                "online", onlinePlayers,
                "total", totalPlayers
        ));

        // Get clan count
        Plugin clansPlugin = Bukkit.getPluginManager().getPlugin("Clans");
        if (clansPlugin != null && clansPlugin.isEnabled()) {
            try {
                Class<?> clansClass = clansPlugin.getClass();
                Method method = clansClass.getMethod("getClanManager");
                Object clanManager = method.invoke(clansPlugin);
                
                Method countMethod = clanManager.getClass().getMethod("getClanCount");
                int clanCount = (int) countMethod.invoke(clanManager);
                
                stats.put("clans", Map.of("total", clanCount));
            } catch (Exception e) {
                stats.put("clans", Map.of("total", 0));
            }
        } else {
            int clanCount = getClanCountFromRedis();
            stats.put("clans", Map.of("total", clanCount));
        }

        // Get economy stats
        Object accountManager = getAccountManager();
        if (accountManager != null) {
            try {
                Method getTopAccountsMethod = accountManager.getClass().getMethod("getTopAccounts", int.class);
                List<?> topAccounts = (List<?>) getTopAccountsMethod.invoke(accountManager, 1);
                
                if (!topAccounts.isEmpty()) {
                    Object richest = topAccounts.get(0);
                    Method getOwnerMethod = richest.getClass().getMethod("getOwner");
                    Method getBalanceMethod = richest.getClass().getMethod("getBalance");
                    
                    UUID uuid = (UUID) getOwnerMethod.invoke(richest);
                    double balance = (double) getBalanceMethod.invoke(richest);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    
                    stats.put("economy", Map.of(
                            "richestPlayer", player.getName() != null ? player.getName() : "Unknown",
                            "richestBalance", balance
                    ));
                }
            } catch (Exception e) {
                // Ignore economy stats if failed
            }
        } else {
            Map<String, Object> richest = getRichestFromRedis();
            if (!richest.isEmpty()) {
                stats.put("economy", richest);
            }
        }

        ctx.json(stats);
    }

    /**
     * Get AccountManager from Economy plugin using reflection
     */
    private Object getAccountManager() {
        Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
        if (economyPlugin == null || !economyPlugin.isEnabled()) {
            return null;
        }

        try {
            Class<?> economyClass = economyPlugin.getClass();
            Method method = economyClass.getMethod("getAccountManager");
            return method.invoke(economyPlugin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Map<String, Object>> getTopPlayersFromRedis(int limit) {
        RedisIO redis = ensureRedis();
        if (redis == null) {
            return List.of();
        }

        Optional<String> payload = redis.getString(ECONOMY_ACCOUNTS_KEY);
        if (payload.isEmpty() || payload.get().isBlank()) {
            return List.of();
        }

        try {
            Type mapType = new TypeToken<Map<String, AccountData>>() {}.getType();
            Map<String, AccountData> accounts = gson.fromJson(payload.get(), mapType);
            if (accounts == null || accounts.isEmpty()) {
                return List.of();
            }

            return accounts.values().stream()
                    .sorted((a, b) -> Double.compare(b.totalBalance(), a.totalBalance()))
                    .limit(limit)
                    .map(this::toLeaderboardEntry)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to read Redis economy leaderboard: " + e.getMessage());
            return List.of();
        }
    }

    private Map<String, Object> toLeaderboardEntry(AccountData data) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(data.uuid);
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("uuid", data.uuid.toString());
        playerData.put("name", player.getName() != null ? player.getName() : "Unknown");
        playerData.put("balance", data.balance);
        playerData.put("bankBalance", data.bankBalance);
        playerData.put("total", data.totalBalance());
        playerData.put("online", player.isOnline());
        return playerData;
    }

    private int getClanCountFromRedis() {
        RedisIO redis = ensureRedis();
        if (redis == null) {
            return 0;
        }

        try {
            Optional<String> payload = redis.getString(CLANS_KEY);
            if (payload.isEmpty() || payload.get().isBlank()) {
                return 0;
            }
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> data = gson.fromJson(payload.get(), listType);
            return data != null ? data.size() : 0;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to read clan count from Redis: " + e.getMessage());
            return 0;
        }
    }

    private Map<String, Object> getRichestFromRedis() {
        RedisIO redis = ensureRedis();
        if (redis == null) {
            return Map.of();
        }

        try {
            Optional<String> payload = redis.getString(ECONOMY_ACCOUNTS_KEY);
            if (payload.isEmpty() || payload.get().isBlank()) {
                return Map.of();
            }

            Type mapType = new TypeToken<Map<String, AccountData>>() {}.getType();
            Map<String, AccountData> accounts = gson.fromJson(payload.get(), mapType);
            if (accounts == null || accounts.isEmpty()) {
                return Map.of();
            }

            AccountData richest = accounts.values().stream()
                    .max(Comparator.comparingDouble(AccountData::totalBalance))
                    .orElse(null);
            if (richest == null) {
                return Map.of();
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(richest.uuid);
            return Map.of(
                    "richestPlayer", player.getName() != null ? player.getName() : "Unknown",
                    "richestBalance", richest.totalBalance()
            );
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to read richest player from Redis: " + e.getMessage());
            return Map.of();
        }
    }

    private RedisIO ensureRedis() {
        if (redisIO != null) {
            return redisIO;
        }

        try {
            RedisConfig cfg = loadRedisConfig();
            redisIO = new RedisIO(cfg, plugin.getLogger());
            redisIO.ping();
            return redisIO;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to init Redis for leaderboards: " + e.getMessage());
            return null;
        }
    }

    private RedisConfig loadRedisConfig() {
        File pluginData = plugin.getDataFolder();
        File serverRoot = pluginData.getParentFile() != null ? pluginData.getParentFile().getParentFile() : null;

        RedisConfig cfg = ConfigStandardConfig.load(serverRoot, new RedisConfig(), plugin.getLogger());

        // Allow override via system properties if needed
        cfg.host = System.getProperty("redis.host", cfg.host);
        cfg.port = Integer.getInteger("redis.port", cfg.port);
        cfg.password = System.getProperty("redis.password", cfg.password);
        cfg.database = Integer.getInteger("redis.database", cfg.database);
        return cfg;
    }

    private static class AccountData {
        UUID uuid;
        double balance;
        double bankBalance;
        long lastInterest;

        double totalBalance() {
            return balance + bankBalance;
        }
    }
}
