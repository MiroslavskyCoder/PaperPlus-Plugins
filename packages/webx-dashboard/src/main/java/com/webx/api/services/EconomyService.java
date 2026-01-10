package com.webx.api.services;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import io.javalin.http.Context;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST API service for Economy plugin integration
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
            EconomyPlugin economy = (EconomyPlugin) Bukkit.getPluginManager().getPlugin("Economy");
            
            if (economy == null) {
                ctx.status(503).json(Map.of(
                    "error", "Economy plugin not loaded",
                    "success", false
                ));
                return;
            }
            
            Account account = economy.getAccountManager().getAccount(uuid);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("uuid", uuid.toString());
            response.put("balance", account.getBalance());
            response.put("bankBalance", account.getBankBalance());
            response.put("totalBalance", account.getTotalBalance());
            
            ctx.json(response);
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of(
                "error", "Invalid UUID format",
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
        
        EconomyPlugin economy = (EconomyPlugin) Bukkit.getPluginManager().getPlugin("Economy");
        
        if (economy == null) {
            ctx.status(503).json(Map.of(
                "error", "Economy plugin not loaded",
                "success", false
            ));
            return;
        }
        
        var topAccounts = economy.getAccountManager().getTopAccounts(limit);
        
        ctx.json(Map.of(
            "success", true,
            "players", topAccounts.stream().map(acc -> Map.of(
                "uuid", acc.getOwner().toString(),
                "balance", acc.getBalance(),
                "totalBalance", acc.getTotalBalance()
            )).toList()
        ));
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
            EconomyPlugin economy = (EconomyPlugin) Bukkit.getPluginManager().getPlugin("Economy");
            
            if (economy == null) {
                ctx.status(503).json(Map.of("error", "Economy plugin not loaded", "success", false));
                return;
            }
            
            boolean success = economy.getAccountManager().deposit(uuid, amount);
            
            ctx.json(Map.of(
                "success", success,
                "message", success ? "Deposited " + amount + " coins" : "Failed to deposit"
            ));
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid UUID format", "success", false));
        }
    }
    
    private static class DepositRequest {
        public double amount;
    }
}
