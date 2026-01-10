package com.webx.randomizer.listeners;

import com.webx.randomizer.RandomizerPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class RandomRewardListener implements Listener {
    private final RandomizerPlugin plugin;
    
    public RandomRewardListener(RandomizerPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Math.random() < 0.1) { // 10% chance
            int reward = plugin.getRandomizerManager().getRandomReward();
            event.getPlayer().sendMessage("§aYou got §6" + reward + "§a coins!");
        }
    }
}
