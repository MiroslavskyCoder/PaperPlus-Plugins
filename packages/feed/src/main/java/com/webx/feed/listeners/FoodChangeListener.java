package com.webx.feed.listeners;

import com.webx.feed.FeedPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodChangeListener implements Listener {
    private final FeedPlugin plugin;
    
    public FoodChangeListener(FeedPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (plugin.getConfig().getBoolean("prevent-starvation", false)) {
            if (event.getFoodLevel() < 5) {
                event.setCancelled(true);
            }
        }
    }
}
