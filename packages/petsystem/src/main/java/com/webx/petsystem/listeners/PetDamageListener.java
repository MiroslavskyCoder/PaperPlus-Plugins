package com.webx.petsystem.listeners;

import com.webx.petsystem.PetSystemPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PetDamageListener implements Listener {
    private final PetSystemPlugin plugin;
    
    public PetDamageListener(PetSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPetDamage(EntityDamageEvent event) {
        // Pet damage handling logic
    }
}
