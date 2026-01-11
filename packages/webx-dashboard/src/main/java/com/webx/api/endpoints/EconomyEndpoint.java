package com.webx.api.endpoints;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Economy API Endpoint
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
            if (!(economyPlugin instanceof EconomyPlugin)) {
                ctx.status(503).json(Map.of(
                    "success", false,
                    "message", "Economy plugin not loaded"
                ));
                return;
            }
            
            EconomyPlugin economy = (EconomyPlugin) economyPlugin;
            Account account = economy.getAccountManager().getAccount(uuid);
            
            if (account == null) {
                ctx.status(404).json(Map.of(
                    "success", false,
                    "message", "Player not found"
                ));
                return;
            }
            
            ctx.json(Map.of(
                "success", true,
                "message", "Success",
                "data", Map.of(
                    "uuid", uuid.toString(),
                    "coins", account.getBalance(),
                    "bankBalance", account.getBankBalance(),
                    "total", account.getTotalBalance()
                )
            ));
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of(
                "success", false,
                "message", "Invalid UUID format"
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
        if (!(economyPlugin instanceof EconomyPlugin)) {
            ctx.status(503).json(Map.of(
                "success", false,
                "message", "Economy plugin not loaded"
            ));
            return;
        }
        
        EconomyPlugin economy = (EconomyPlugin) economyPlugin;
        List<Account> topAccounts = economy.getAccountManager().getTopAccounts(limit);
        
        List<Map<String, Object>> data = new ArrayList<>();
        for (Account account : topAccounts) {
            data.add(Map.of(
                "uuid", account.getOwner().toString(),
                "coins", account.getBalance(),
                "bankBalance", account.getBankBalance(),
                "total", account.getTotalBalance()
            ));
        }
        
        ctx.json(Map.of(
            "success", true,
            "message", "Success",
            "data", data
        ));
    }
}
