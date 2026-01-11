package com.webx.api.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.economy.managers.AccountManager;
import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * REST API service for Leaderboards
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

        AccountManager accountManager = getAccountManager();
        if (accountManager == null) {
            ctx.status(503).json(Map.of("error", "Economy plugin not available"));
            return;
        }

        List<Map.Entry<UUID, Double>> topAccounts = accountManager.getTopAccounts(limit);
        List<Map<String, Object>> leaderboard = new ArrayList<>();

        int position = 1;
        for (Map.Entry<UUID, Double> entry : topAccounts) {
            UUID uuid = entry.getKey();
            double balance = entry.getValue();
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
                java.lang.reflect.Method method = clansClass.getMethod("getClanManager");
                Object clanManager = method.invoke(clansPlugin);
                
                java.lang.reflect.Method countMethod = clanManager.getClass().getMethod("getClanCount");
                int clanCount = (int) countMethod.invoke(clanManager);
                
                stats.put("clans", Map.of("total", clanCount));
            } catch (Exception e) {
                stats.put("clans", Map.of("total", 0));
            }
        } else {
            stats.put("clans", Map.of("total", 0));
        }

        // Get economy stats
        AccountManager accountManager = getAccountManager();
        if (accountManager != null) {
            List<Map.Entry<UUID, Double>> topAccounts = accountManager.getTopAccounts(1);
            if (!topAccounts.isEmpty()) {
                Map.Entry<UUID, Double> richest = topAccounts.get(0);
                OfflinePlayer player = Bukkit.getOfflinePlayer(richest.getKey());
                
                stats.put("economy", Map.of(
                        "richestPlayer", player.getName() != null ? player.getName() : "Unknown",
                        "richestBalance", richest.getValue()
                ));
            }
        }

        ctx.json(stats);
    }

    /**
     * Get AccountManager from Economy plugin
     */
    private AccountManager getAccountManager() {
        Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
        if (economyPlugin == null || !economyPlugin.isEnabled()) {
            return null;
        }

        try {
            Class<?> economyClass = economyPlugin.getClass();
            java.lang.reflect.Method method = economyClass.getMethod("getAccountManager");
            return (AccountManager) method.invoke(economyPlugin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
