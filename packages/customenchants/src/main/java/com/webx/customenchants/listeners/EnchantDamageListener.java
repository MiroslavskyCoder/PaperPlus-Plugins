package com.webx.customenchants.listeners;

import com.webx.customenchants.CustomEnchantsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EnchantDamageListener implements Listener {
    private final CustomEnchantsPlugin plugin;
    
    public EnchantDamageListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        // Apply enchant bonuses
    }
}
