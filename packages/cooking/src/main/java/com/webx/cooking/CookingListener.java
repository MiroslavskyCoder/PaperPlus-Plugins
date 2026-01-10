package com.webx.cooking;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCookEvent;

public class CookingListener implements Listener {
    private CookingManager manager;
    
    public CookingListener(CookingManager manager) {
        this.manager = manager;
    }
    
    @EventHandler
    public void onPlayerCook(PlayerCookEvent event) {
        manager.learnRecipe(event.getPlayer(), event.getRecipe());
    }
}
