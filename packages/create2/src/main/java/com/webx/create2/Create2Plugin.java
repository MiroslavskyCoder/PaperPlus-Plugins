package com.webx.create2;

import com.webx.create2.kinematic.KinematicNetworkManager;
import com.webx.create2.kinematic.RotationPropagator;
import com.webx.create2.commands.Create2Command;
import com.webx.create2.listeners.BlockListener;
import com.webx.create2.logistics.LogisticsManager;
import com.webx.create2.fluid.FluidManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Create2 - Mechanical contraptions and automation
 * Inspired by Creators-of-Create/Create
 * 
 * Original implementation for Paper API
 */
public class Create2Plugin extends JavaPlugin {
    
    private static Create2Plugin instance;
    private KinematicNetworkManager networkManager;
    private RotationPropagator rotationPropagator;
    private LogisticsManager logisticsManager;
    private FluidManager fluidManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        initializeManagers();

        // Load persistence
        networkManager.loadAll();
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        // Start kinematic tick
        startKinematicTick();
        
        getLogger().info("Create2 enabled! Mechanical systems online.");
        getLogger().info("Max network size: " + getConfig().getInt("kinematic.max-network-size"));
        getLogger().info("Max RPM: " + getConfig().getDouble("rotation.max-rpm"));
    }
    
    @Override
    public void onDisable() {
        // Save all networks
        if (networkManager != null) {
            networkManager.saveAll();
            getLogger().info("Saved " + networkManager.getNetworkCount() + " kinematic networks");
        }
        
        // Cancel tasks
        Bukkit.getScheduler().cancelTasks(this);
        
        getLogger().info("Create2 disabled. Mechanical systems offline.");
    }
    
    private void initializeManagers() {
        this.networkManager = new KinematicNetworkManager(this);
        this.rotationPropagator = new RotationPropagator(this);
        this.logisticsManager = new LogisticsManager(this, networkManager);
        this.fluidManager = new FluidManager(this);
        
        getLogger().info("Initialized kinematic systems");
    }
    
    private void registerCommands() {
        getCommand("create2").setExecutor(new Create2Command(this));
        getLogger().info("Registered commands");
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getLogger().info("Registered event listeners");
    }
    
    private void startKinematicTick() {
        int tickRate = getConfig().getInt("kinematic.update-tick-rate", 1);
        
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            long startTime = System.nanoTime();
            
            // Update all kinematic networks
            networkManager.tick();

            // Update logistics (belts, funnels, deployers)
            if (!getServer().getWorlds().isEmpty()) {
                logisticsManager.tick(getServer().getWorlds().get(0));
            }

            // Update fluids
            if (!getServer().getWorlds().isEmpty()) {
                fluidManager.tick(getServer().getWorlds().get(0));
            }
            
            long duration = System.nanoTime() - startTime;
            double ms = duration / 1_000_000.0;
            
            // Warn if taking too long
            double maxTime = getConfig().getDouble("performance.max-tick-time", 10.0);
            if (ms > maxTime) {
                getLogger().warning("Kinematic tick took " + String.format("%.2f", ms) + "ms (max: " + maxTime + "ms)");
            }
        }, tickRate, tickRate);
        
        getLogger().info("Started kinematic tick (rate: " + tickRate + " ticks)");
    }
    
    // Getters
    public static Create2Plugin getInstance() {
        return instance;
    }
    
    public KinematicNetworkManager getNetworkManager() {
        return networkManager;
    }
    
    public RotationPropagator getRotationPropagator() {
        return rotationPropagator;
    }

    public LogisticsManager getLogisticsManager() {
        return logisticsManager;
    }

    public FluidManager getFluidManager() {
        return fluidManager;
    }
    
    // Utility methods
    public double getMaxRPM() {
        return getConfig().getDouble("rotation.max-rpm", 256.0);
    }
    
    public double getMaxStress() {
        return getConfig().getDouble("kinematic.max-stress", 10000.0);
    }
    
    public int getMaxNetworkSize() {
        return getConfig().getInt("kinematic.max-network-size", 1000);
    }
    
    public boolean isDebugEnabled() {
        return getConfig().getBoolean("debug.show-networks", false);
    }
}
