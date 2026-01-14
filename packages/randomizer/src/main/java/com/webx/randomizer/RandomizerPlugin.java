package com.webx.randomizer;

import com.webx.randomizer.commands.RandomCommand;
import com.webx.randomizer.managers.RandomizerManager;
import com.webx.randomizer.listeners.RandomRewardListener;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Randomizer Plugin
 * - Random teleport to safe surface location (no monsters)
 * - Random number generation
 * - Random item rewards (wood types and sticks only)
 * - 3 minute cooldown between rewards
 */
public class RandomizerPlugin extends JavaPlugin {
    private static RandomizerPlugin instance;
    private RandomizerManager randomizerManager;
    private RandomRewardListener rewardListener;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // Initialize managers
        randomizerManager = new RandomizerManager(this);
        rewardListener = new RandomRewardListener(this, randomizerManager);
        
        // Register events
        getServer().getPluginManager().registerEvents(rewardListener, this);
        
        // Register commands
        getCommand("randomizer").setExecutor(new RandomCommand(this, randomizerManager));
        getCommand("randomtp").setExecutor(new RandomCommand(this, randomizerManager));
        getCommand("randomitem").setExecutor(new RandomCommand(this, randomizerManager));
        getCommand("randomnumber").setExecutor(new RandomCommand(this, randomizerManager));
        
        getLogger().info("Randomizer Plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Randomizer Plugin disabled!");
    }
    
    public static RandomizerPlugin getInstance() {
        return instance;
    }

    public RandomizerManager getRandomizerManager() {
        return randomizerManager;
    }
}
