package com.webx.api.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;

/**
 * REST API service for Leaderboards using reflection
 */
public class LeaderboardService {
    private final Gson gson;

    public LeaderboardService() {
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
            ctx.status(503).json(Map.of("error", "Economy plugin not available"));
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
            stats.put("clans", Map.of("total", 0));
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
}
