package com.webx.deathmessage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    
    private final DeathMessagePlugin plugin;
    
    public DeathListener(DeathMessagePlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.isEnabled()) return;
        
        Player player = event.getEntity();
        String deathMessage = buildDeathMessage(player, event);
        
        // Replace default death message
        event.setDeathMessage(null);
        
        // Broadcast or send to player
        if (plugin.isBroadcast()) {
            Bukkit.broadcastMessage(deathMessage);
        } else {
            player.sendMessage(deathMessage);
        }
    }
    
    private String buildDeathMessage(Player player, PlayerDeathEvent event) {
        String message;
        String causeName = "default";
        String killerName = "";
        
        // Determine death cause
        if (player.getKiller() != null) {
            causeName = "pvp";
            killerName = player.getKiller().getName();
        } else if (player.getLastDamageCause() != null) {
            switch (player.getLastDamageCause().getCause()) {
                case FALL:
                    causeName = "fall";
                    break;
                case DROWNING:
                    causeName = "drowning";
                    break;
                case FIRE:
                case FIRE_TICK:
                    causeName = "fire";
                    break;
                case LAVA:
                    causeName = "lava";
                    break;
                case SUFFOCATION:
                    causeName = "suffocation";
                    break;
                case VOID:
                    causeName = "void";
                    break;
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    causeName = "explosion";
                    break;
                case MAGIC:
                case POISON:
                    causeName = "magic";
                    break;
                case WITHER:
                    causeName = "wither";
                    break;
                case FALLING_BLOCK:
                    causeName = "falling-block";
                    break;
                case ENTITY_ATTACK:
                    causeName = "mob";
                    Entity damager = player.getLastDamageCause().getEntity();
                    if (damager != null) {
                        killerName = damager.getName();
                    }
                    break;
                case PROJECTILE:
                    causeName = "projectile";
                    if (player.getLastDamageCause().getEntity() instanceof Projectile) {
                        Projectile proj = (Projectile) player.getLastDamageCause().getEntity();
                        if (proj.getShooter() instanceof Entity) {
                            killerName = ((Entity) proj.getShooter()).getName();
                        }
                    }
                    break;
                default:
                    causeName = "default";
            }
        }
        
        // Get message template
        message = plugin.getCauseMessages().getOrDefault(causeName, plugin.getMessageFormat());
        
        // Replace placeholders
        message = message.replace("%player%", player.getName());
        message = message.replace("%killer%", killerName);
        message = message.replace("%cause%", causeName);
        
        // Add location
        if (plugin.isShowLocation()) {
            Location loc = player.getLocation();
            String coords = String.format(" §8[§7%d, %d, %d§8]", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            message += coords;
        }
        
        // Add world
        if (plugin.isShowWorld()) {
            message += " §8in §7" + player.getWorld().getName();
        }
        
        return message;
    }
}
