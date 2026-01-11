package com.webx.clans.listeners;

import com.webx.clans.ClansPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private final ClansPlugin plugin;

    public PlayerMoveListener(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        String claimingClan = plugin.getTerritoryManager().getClaimingClan(event.getTo().getChunk());
        // TODO: Send clan territory notifications
    }
}
