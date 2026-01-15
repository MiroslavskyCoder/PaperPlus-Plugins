package com.webx.tournaments.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TournamentMatchListener implements Listener {
    // TODO: Handle match events (win, lose, forfeit, etc.)
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // TODO: Forfeit match if player leaves
    }
}
