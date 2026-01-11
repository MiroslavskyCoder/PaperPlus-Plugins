package com.webx.api.endpoints;

import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Economy API Endpoint using reflection
 * Provides player coins information for Web Dashboard
 */
public class EconomyEndpoint {
    private final JavaPlugin plugin;
    
    public EconomyEndpoint(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * GET /api/player/{uuid}/coins
     * Get player coins information
     */
    public void getPlayerCoins(Context ctx) {
        String uuidStr = ctx.pathParam("uuid");
        
        try {
            UUID uuid = UUID.fromString(uuidStr);
            
            // Get Economy plugin
            Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
            if (economyPlugin == null || !economyPlugin.isEnabled()) {
                ctx.status(503).json(Map.of(
                    "success", false,
                    "message", "Economy plugin not loaded"
                ));
                return;
            }
            
            // Get AccountManager
            Method getAccountManagerMethod = economyPlugin.getClass().getMethod("getAccountManager");
            Object accountManager = getAccountManagerMethod.invoke(economyPlugin);
            
            // Get Account
            Method getAccountMethod = accountManager.getClass().getMethod("getAccount", UUID.class);
            Object account = getAccountMethod.invoke(accountManager, uuid);
            
            if (account == null) {
                ctx.status(404).json(Map.of(
                    "success", false,
                    "message", "Player not found"
                ));
                return;
            }
            
            // Get balances
            Method getBalanceMethod = account.getClass().getMethod("getBalance");
            Method getBankBalanceMethod = account.getClass().getMethod("getBankBalance");
            Method getTotalBalanceMethod = account.getClass().getMethod("getTotalBalance");
            
            double balance = (double) getBalanceMethod.invoke(account);
            double bankBalance = (double) getBankBalanceMethod.invoke(account);
            double totalBalance = (double) getTotalBalanceMethod.invoke(account);
            
            ctx.json(Map.of(
                "success", true,
                "message", "Success",
                "data", Map.of(
                    "uuid", uuid.toString(),
                    "coins", balance,
                    "bankBalance", bankBalance,
                    "total", totalBalance
                )
            ));
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of(
                "success", false,
                "message", "Invalid UUID format"
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                "success", false,
                "message", "Internal error: " + e.getMessage()
            ));
        }
    }
    
    /**
     * GET /api/players/top?limit=10
     * Get top players by balance
     */
    public void getTopPlayers(Context ctx) {
        int limit = Integer.parseInt(ctx.queryParamAsClass("limit", String.class).getOrDefault("10"));
        
        Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
        if (economyPlugin == null || !economyPlugin.isEnabled()) {
            ctx.status(503).json(Map.of(
                "success", false,
                "message", "Economy plugin not loaded"
            ));
            return;
        }
        
        try {
            // Get AccountManager
            Method getAccountManagerMethod = economyPlugin.getClass().getMethod("getAccountManager");
            Object accountManager = getAccountManagerMethod.invoke(economyPlugin);
            
            // Get top accounts
            Method getTopAccountsMethod = accountManager.getClass().getMethod("getTopAccounts", int.class);
            List<?> topAccounts = (List<?>) getTopAccountsMethod.invoke(accountManager, limit);
            
            List<Map<String, Object>> data = new ArrayList<>();
            for (Object account : topAccounts) {
                Method getOwnerMethod = account.getClass().getMethod("getOwner");
                Method getBalanceMethod = account.getClass().getMethod("getBalance");
                Method getBankBalanceMethod = account.getClass().getMethod("getBankBalance");
                Method getTotalBalanceMethod = account.getClass().getMethod("getTotalBalance");
                
                UUID uuid = (UUID) getOwnerMethod.invoke(account);
                double balance = (double) getBalanceMethod.invoke(account);
                double bankBalance = (double) getBankBalanceMethod.invoke(account);
                double totalBalance = (double) getTotalBalanceMethod.invoke(account);
                
                data.add(Map.of(
                    "uuid", uuid.toString(),
                    "coins", balance,
                    "bankBalance", bankBalance,
                    "total", totalBalance
                ));
            }
            
            ctx.json(Map.of(
                "success", true,
                "message", "Success",
                "data", data
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                "success", false,
                "message", "Internal error: " + e.getMessage()
            ));
        }
    }
}
