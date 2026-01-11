package com.webx.backtp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportListener implements Listener {
    private final BackTpPlugin plugin;
    private final TeleportManager manager;

    public TeleportListener(BackTpPlugin plugin, TeleportManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!manager.hasPendingTeleport(player)) return;

        String type = manager.getTeleportType(player);
        if (type == null) return;

        String configPath = type.equals("back") ? "back.cancel-on-move" : "tpa.cancel-on-move";
        if (!plugin.getConfig().getBoolean(configPath, true)) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (to == null) return;
        
        if (from.getBlockX() != to.getBlockX() || 
            from.getBlockY() != to.getBlockY() || 
            from.getBlockZ() != to.getBlockZ()) {
            
            manager.cancelTeleport(player);
            String msgKey = type.equals("back") ? "messages.back.cancelled" : "messages.tpa.cancelled";
            String msg = plugin.getConfig().getString(msgKey, "&cТелепортация отменена!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg + " Вы сдвинулись с места."));
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!manager.hasPendingTeleport(player)) return;

        String type = manager.getTeleportType(player);
        if (type == null) return;

        String configPath = type.equals("back") ? "back.cancel-on-damage" : "tpa.cancel-on-damage";
        if (!plugin.getConfig().getBoolean(configPath, true)) return;

        manager.cancelTeleport(player);
        String msgKey = type.equals("back") ? "messages.back.cancelled" : "messages.tpa.cancelled";
        String msg = plugin.getConfig().getString(msgKey, "&cТелепортация отменена!");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg + " Вы получили урон."));
    }
}
