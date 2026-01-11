package com.webx.hometp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportListener implements Listener {
    private final TeleportManager manager;

    public TeleportListener(TeleportManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!manager.hasPendingTeleport(player)) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (to == null) return;
        
        // Only cancel if player moved more than slightly (ignore head rotation)
        if (from.getBlockX() != to.getBlockX() || 
            from.getBlockY() != to.getBlockY() || 
            from.getBlockZ() != to.getBlockZ()) {
            
            manager.cancelTeleport(player);
            String msg = ChatColor.translateAlternateColorCodes('&', 
                "&cТелепортация отменена! Вы сдвинулись с места.");
            player.sendMessage(msg);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!manager.hasPendingTeleport(player)) return;

        manager.cancelTeleport(player);
        String msg = ChatColor.translateAlternateColorCodes('&', 
            "&cТелепортация отменена! Вы получили урон.");
        player.sendMessage(msg);
    }
}
