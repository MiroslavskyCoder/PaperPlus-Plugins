package com.webx.claims;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ClaimsPlugin extends JavaPlugin implements Listener {
    private static ClaimsPlugin instance;
    private Map<String, Claim> claims = new HashMap<>();
    private Map<UUID, Set<String>> playerClaims = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        
        getCommand("claim").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                player.sendMessage("§cUsage: /claim [create/delete/list]");
                return true;
            }
            
            switch (args[0].toLowerCase()) {
                case "create":
                    claimChunk(player);
                    break;
                case "delete":
                    unclaimChunk(player);
                    break;
                case "list":
                    listClaims(player);
                    break;
            }
            
            return true;
        });
        
        getLogger().info("Claims Plugin enabled!");
    }
    
    private void claimChunk(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        String key = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        
        if (claims.containsKey(key)) {
            player.sendMessage("§cThis chunk is already claimed!");
            return;
        }
        
        Claim claim = new Claim(key, player.getUniqueId());
        claims.put(key, claim);
        playerClaims.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(key);
        
        player.sendMessage("§aChunk claimed!");
    }
    
    private void unclaimChunk(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        String key = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        
        Claim claim = claims.get(key);
        if (claim == null || !claim.owner.equals(player.getUniqueId())) {
            player.sendMessage("§cYou don't own this chunk!");
            return;
        }
        
        claims.remove(key);
        playerClaims.get(player.getUniqueId()).remove(key);
        
        player.sendMessage("§aChunk unclaimed!");
    }
    
    private void listClaims(Player player) {
        Set<String> playerClaimSet = playerClaims.getOrDefault(player.getUniqueId(), new HashSet<>());
        player.sendMessage("§a=== Your Claims: " + playerClaimSet.size() + " ===");
        for (String claim : playerClaimSet) {
            player.sendMessage("  §f" + claim);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        String key = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        
        Claim claim = claims.get(key);
        if (claim != null && !claim.owner.equals(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cThis chunk is claimed by another player!");
        }
    }
    
    public static ClaimsPlugin getInstance() {
        return instance;
    }
    
    private static class Claim {
        String id;
        UUID owner;
        
        Claim(String id, UUID owner) {
            this.id = id;
            this.owner = owner;
        }
    }
}
