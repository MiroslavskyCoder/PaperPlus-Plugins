package com.webx.warps.listeners;

import com.webx.warps.WarpsPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportListener implements Listener {
    private final WarpsPlugin plugin;

    public TeleportListener(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.getConfig().getBoolean("teleport.cancel-on-move", true)) {
            return;
        }

        Player player = event.getPlayer();
        if (!plugin.getTeleportManager().hasPendingTeleport(player)) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;

        if (from.getBlockX() != to.getBlockX() ||
            from.getBlockY() != to.getBlockY() ||
            from.getBlockZ() != to.getBlockZ()) {

            plugin.getTeleportManager().cancelTeleport(player);
            plugin.getMessageManager().send(player, "cancelled");
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!plugin.getConfig().getBoolean("teleport.cancel-on-damage", true)) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!plugin.getTeleportManager().hasPendingTeleport(player)) {
            return;
        }

        plugin.getTeleportManager().cancelTeleport(player);
        plugin.getMessageManager().send(player, "cancelled");
    }
}
