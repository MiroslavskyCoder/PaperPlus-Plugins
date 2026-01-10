package com.webx.dashboard.endpoints;

import com.google.gson.Gson;
import com.webx.dashboard.WebDashboardPlugin;
import com.webx.dashboard.api.WebApiServer.Response;
import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PlayerEndpoint {
    private final WebDashboardPlugin plugin;
    private final Gson gson;
    
    public PlayerEndpoint(WebDashboardPlugin plugin, Gson gson) {
        this.plugin = plugin;
        this.gson = gson;
    }
    
    public void getPlayerCoins(Context ctx) {
        String uuidStr = ctx.pathParam("uuid");
        
        try {
            UUID uuid = UUID.fromString(uuidStr);
            
            // Get Economy plugin
            Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
            if (!(economyPlugin instanceof EconomyPlugin)) {
                ctx.status(503).json(new Response(false, "Economy plugin not loaded"));
                return;
            }
            
            EconomyPlugin economy = (EconomyPlugin) economyPlugin;
            Account account = economy.getAccountManager().getAccount(uuid);
            
            if (account == null) {
                ctx.status(404).json(new Response(false, "Player not found"));
                return;
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("uuid", uuid.toString());
            data.put("coins", account.getBalance());
            data.put("bankBalance", account.getBankBalance());
            data.put("total", account.getTotalBalance());
            
            ctx.json(new Response(true, "Success", data));
            
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(new Response(false, "Invalid UUID format"));
        }
    }
    
    public void getTopPlayers(Context ctx) {
        int limit = Integer.parseInt(ctx.queryParamAsClass("limit", String.class).getOrDefault("10"));
        
        Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
        if (!(economyPlugin instanceof EconomyPlugin)) {
            ctx.status(503).json(new Response(false, "Economy plugin not loaded"));
            return;
        }
        
        EconomyPlugin economy = (EconomyPlugin) economyPlugin;
        List<Account> topAccounts = economy.getAccountManager().getTopAccounts(limit);
        
        List<Map<String, Object>> data = new ArrayList<>();
        for (Account account : topAccounts) {
            Map<String, Object> playerData = new HashMap<>();
            playerData.put("uuid", account.getOwner().toString());
            playerData.put("coins", account.getBalance());
            playerData.put("bankBalance", account.getBankBalance());
            playerData.put("total", account.getTotalBalance());
            data.add(playerData);
        }
        
        ctx.json(new Response(true, "Success", data));
    }
}
