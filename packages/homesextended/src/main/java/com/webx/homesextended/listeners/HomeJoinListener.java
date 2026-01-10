package com.webx.homesextended.listeners;

import com.webx.homesextended.HomesExtendedPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HomeJoinListener implements Listener {
    private final HomesExtendedPlugin plugin;
    
    public HomeJoinListener(HomesExtendedPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§6Welcome! Use /home to teleport to your homes.");
    }
}
