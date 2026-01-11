package com.webx.warps.listeners;

import com.webx.warps.WarpsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final WarpsPlugin plugin;

    public PlayerJoinListener(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Clear any pending cooldowns or teleports for rejoining players
        plugin.getCooldownManager().removeCooldown(event.getPlayer());
        plugin.getTeleportManager().cancelTeleport(event.getPlayer());
    }
}
