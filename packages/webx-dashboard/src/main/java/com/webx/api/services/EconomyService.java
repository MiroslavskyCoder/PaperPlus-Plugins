package com.webx.api.services;

import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST API service for Economy plugin integration using reflection
 * Provides endpoints for player coins and balance information
 */
public class EconomyService {
    
    /**
     * GET /api/economy/player/:uuid
     * Returns player's coin balance
     */
    public static void getPlayerCoins(Context ctx) {
        String uuidStr = ctx.pathParam("uuid");
        
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Plugin economy = Bukkit.getPluginManager().getPlugin("Economy");
            
            if (economy == null) {
                ctx.status(503).json(Map.of(
                    "error", "Economy plugin not loaded",
                    "success", false
                ));
                return;
            }
            
            // Get AccountManager
            Method getAccountManagerMethod = economy.getClass().getMethod("getAccountManager");
            Object accountManager = getAccountManagerMethod.invoke(economy);
            
            // Get Account
            Method getAccountMethod = accountManager.getClass().getMethod("getAccount", UUID.class);
            Object account = getAccountMethod.invoke(accountManager, uuid);
            
            // Get balances
            Method getBalanceMethod = account.getClass().getMethod("getBalance");
            Method getBankBalanceMethod = account.getClass().getMethod("getBankBalance");
            Method getTotalBalanceMethod = account.getClass().getMethod("getTotalBalance");
            
            double balance = (double) getBalanceMethod.invoke(account);
            double bankBalance = (double) getBankBalanceMethod.invoke(account);
            double totalBalance = (double) getTotalBalanceMethod.invoke(account);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("uuid", uuid.toString());
            response.put("balance", balance);
            response.put("bankBalance", bankBalance);
            response.put("totalBalance", totalBalance);
            
            ctx.json(response);
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of(
                "error", "Invalid UUID format",
                "success", false
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                "error", "Failed to get player coins: " + e.getMessage(),
                "success", false
            ));
        }
    }
    
    /**
     * GET /api/economy/top
     * Returns top players by balance
     */
    public static void getTopPlayers(Context ctx) {
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);
        
        Plugin economy = Bukkit.getPluginManager().getPlugin("Economy");
        
        if (economy == null) {
            ctx.status(503).json(Map.of(
                "error", "Economy plugin not loaded",
                "success", false
            ));
            return;
        }
        
        try {
            // Get AccountManager
            Method getAccountManagerMethod = economy.getClass().getMethod("getAccountManager");
            Object accountManager = getAccountManagerMethod.invoke(economy);
            
            // Get top accounts
            Method getTopAccountsMethod = accountManager.getClass().getMethod("getTopAccounts", int.class);
            List<?> topAccounts = (List<?>) getTopAccountsMethod.invoke(accountManager, limit);
            
            List<Map<String, Object>> players = new ArrayList<>();
            for (Object acc : topAccounts) {
                Method getOwnerMethod = acc.getClass().getMethod("getOwner");
                Method getBalanceMethod = acc.getClass().getMethod("getBalance");
                Method getTotalBalanceMethod = acc.getClass().getMethod("getTotalBalance");
                
                UUID uuid = (UUID) getOwnerMethod.invoke(acc);
                double balance = (double) getBalanceMethod.invoke(acc);
                double totalBalance = (double) getTotalBalanceMethod.invoke(acc);
                
                players.add(Map.of(
                    "uuid", uuid.toString(),
                    "balance", balance,
                    "totalBalance", totalBalance
                ));
            }
            
            ctx.json(Map.of(
                "success", true,
                "players", players
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                "error", "Failed to get top players: " + e.getMessage(),
                "success", false
            ));
        }
    }
    
    /**
     * POST /api/economy/player/:uuid/deposit
     * Deposits coins to player account
     */
    public static void depositCoins(Context ctx) {
        String uuidStr = ctx.pathParam("uuid");
        double amount = ctx.bodyAsClass(DepositRequest.class).amount;
        
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Plugin economy = Bukkit.getPluginManager().getPlugin("Economy");
            
            if (economy == null) {
                ctx.status(503).json(Map.of("error", "Economy plugin not loaded", "success", false));
                return;
            }
            
            // Get AccountManager
            Method getAccountManagerMethod = economy.getClass().getMethod("getAccountManager");
            Object accountManager = getAccountManagerMethod.invoke(economy);
            
            // Deposit
            Method depositMethod = accountManager.getClass().getMethod("deposit", UUID.class, double.class);
            boolean success = (boolean) depositMethod.invoke(accountManager, uuid, amount);
            
            ctx.json(Map.of(
                "success", success,
                "message", success ? "Deposited " + amount + " coins" : "Failed to deposit"
            ));
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid UUID format", "success", false));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to deposit: " + e.getMessage(), "success", false));
        }
    }
    
    private static class DepositRequest {
        public double amount;
    }
}
