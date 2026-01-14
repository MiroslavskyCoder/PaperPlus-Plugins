package com.webx.randomizer.listeners;

import com.webx.randomizer.RandomizerPlugin;
import com.webx.randomizer.managers.RandomizerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Event listener for random rewards
 */
public class RandomRewardListener implements Listener {
    private final RandomizerPlugin plugin;
    private final RandomizerManager manager;
    
    public RandomRewardListener(RandomizerPlugin plugin, RandomizerManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }
    
    /**
     * Welcome message on join
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Send info about randomizer commands
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage("§6Используйте §a/randomizer§6 чтобы увидеть доступные команды!");
        }, 60L); // 3 seconds delay
    }
}
