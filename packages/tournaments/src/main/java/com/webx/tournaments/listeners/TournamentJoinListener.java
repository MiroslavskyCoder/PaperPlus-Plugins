package com.webx.tournaments.listeners;

import com.webx.tournaments.TournamentsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TournamentJoinListener implements Listener {
    private final TournamentsPlugin plugin;
    
    public TournamentJoinListener(TournamentsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§6Check /tournament for active tournaments!");
    }
}
