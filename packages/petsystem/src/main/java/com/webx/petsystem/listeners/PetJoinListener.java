package com.webx.petsystem.listeners;

import com.webx.petsystem.PetSystemPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PetJoinListener implements Listener {
    private final PetSystemPlugin plugin;
    
    public PetJoinListener(PetSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var pets = plugin.getPetManager().getPets(event.getPlayer());
        if (!pets.isEmpty()) {
            event.getPlayer().sendMessage("§6You have §f" + pets.size() + "§6 pets!");
        }
    }
}
