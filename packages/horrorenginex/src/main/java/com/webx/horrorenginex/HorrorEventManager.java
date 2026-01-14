package com.webx.horrorenginex;

import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages horror events that occur periodically
 */
public class HorrorEventManager implements Listener {
    
    private final HorrorEngineXPlugin plugin;
    private BukkitTask eventScheduler;
    private static final long TICK_INTERVAL = 60; // Check every 3 seconds
    
    public HorrorEventManager(HorrorEngineXPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Start the horror event scheduler
     */
    public void startEventScheduler() {
        if (eventScheduler != null) {
            eventScheduler.cancel();
        }
        
        eventScheduler = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
            plugin,
            this::checkForHorrorEvents,
            TICK_INTERVAL,
            TICK_INTERVAL
        );
    }
    
    /**
     * Stop the horror event scheduler
     */
    public void stopEventScheduler() {
        if (eventScheduler != null) {
            eventScheduler.cancel();
            eventScheduler = null;
        }
    }
    
    /**
     * Check if horror events should occur
     */
    private void checkForHorrorEvents() {
        if (!plugin.getConfigManager().isHorrorEventsEnabled()) {
            return;
        }
        
        // Events trigger randomly for each player
        // Actual triggering is handled in onPlayerMove
    }
    
    /**
     * Trigger a specific horror event
     */
    public void triggerEvent(String eventName) {
        // Can be extended to trigger specific named events
        plugin.getLogger().info("Horror event triggered: " + eventName);
    }
    
    /**
     * Check if event scheduler is running
     */
    public boolean isRunning() {
        return eventScheduler != null && !eventScheduler.isCancelled();
    }
}
