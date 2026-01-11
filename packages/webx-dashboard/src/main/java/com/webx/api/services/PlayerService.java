package com.webx.api.services;

import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerService {
    
    private final JavaPlugin plugin;
    
    public PlayerService(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void kickPlayer(String playerUuid, String reason) {
        if (reason == null) reason = "Kicked by admin";
        
        try {
            UUID uuid = UUID.fromString(playerUuid);
            String finalReason = reason;
            
            new BukkitRunnable() {
                public void run() {
                    org.bukkit.entity.Player player = plugin.getServer().getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        player.kick(net.kyori.adventure.text.Component.text(finalReason));
                        plugin.getLogger().info("👢 Kicked player: " + player.getName() + " Reason: " + finalReason);
                    }
                }
            }.runTask(plugin);
        } catch (Exception e) {
            throw new RuntimeException("Error kicking player: " + e.getMessage());
        }
    }
    
    public void banPlayer(String playerUuid, String reason) {
        if (reason == null) reason = "Banned by admin";
        
        try {
            UUID uuid = UUID.fromString(playerUuid);
            String finalReason = reason;
            
            new BukkitRunnable() {
                public void run() {
                    org.bukkit.entity.Player player = plugin.getServer().getPlayer(uuid);
                    if (player != null) {
                        // Ban the player using the modern API
                        java.util.Date expires = new java.util.Date(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(36500));
                        @SuppressWarnings({"deprecation", "unused"})
                        var banList = plugin.getServer().getBanList(org.bukkit.BanList.Type.NAME);
                        @SuppressWarnings("deprecation")
                        var ignored = banList.addBan(player.getName(), finalReason, expires, "Admin");
                        if (player.isOnline()) {
                            player.kick(net.kyori.adventure.text.Component.text("You have been banned: " + finalReason));
                        }
                        plugin.getLogger().info("🚫 Banned player: " + player.getName() + " Reason: " + finalReason);
                    }
                }
            }.runTask(plugin);
        } catch (Exception e) {
            throw new RuntimeException("Error banning player: " + e.getMessage());
        }
    }
    
    public void teleportPlayer(String playerUuid) {
        try {
            UUID uuid = UUID.fromString(playerUuid);
            
            new BukkitRunnable() {
                public void run() {
                    org.bukkit.entity.Player player = plugin.getServer().getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
                        plugin.getLogger().info("📍 Teleported player: " + player.getName() + " to spawn");
                    }
                }
            }.runTask(plugin);
        } catch (Exception e) {
            throw new RuntimeException("Error teleporting player: " + e.getMessage());
        }
    }
    
    public void healPlayer(String playerUuid) {
        try {
            UUID uuid = UUID.fromString(playerUuid);
            
            new BukkitRunnable() {
                public void run() {
                    org.bukkit.entity.Player player = plugin.getServer().getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        player.setHealth(20.0);
                        player.setFoodLevel(20);
                        plugin.getLogger().info("💊 Healed player: " + player.getName());
                    }
                }
            }.runTask(plugin);
        } catch (Exception e) {
            throw new RuntimeException("Error healing player: " + e.getMessage());
        }
    }
}
