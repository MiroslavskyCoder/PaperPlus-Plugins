package com.webx.feed.managers;

import org.bukkit.entity.Player;
import java.util.*;

public class FeedManager {
    private final Map<UUID, Integer> hungerLevels = new HashMap<>();
    private final int hungerRestore;
    
    public FeedManager(int hungerRestore) {
        this.hungerRestore = hungerRestore;
    }
    
    public void feedPlayer(Player player) {
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + hungerRestore));
        player.sendMessage("§aYou have been fed!");
    }
    
    public void feedAll(Collection<? extends Player> players) {
        for (Player player : players) {
            feedPlayer(player);
        }
    }
}
