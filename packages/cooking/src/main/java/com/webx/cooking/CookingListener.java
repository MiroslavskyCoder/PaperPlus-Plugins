package com.webx.cooking;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CookingListener implements Listener {
    private CookingManager manager;
    
    public CookingListener(CookingManager manager) {
        this.manager = manager;
    }
    
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String recipeKey = event.getRecipe().getResult().getType().name();
        manager.learnRecipe(player, recipeKey);
    }
}
