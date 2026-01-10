package com.webx.fishing;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListener implements Listener {
    private FishingManager manager;
    
    public FishingListener(FishingManager manager) {
        this.manager = manager;
    }
    
    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            manager.addFish(event.getPlayer(), 1);
        }
    }
}
