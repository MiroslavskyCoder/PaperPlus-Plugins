package com.webx.horrorenginex;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * HorrorEngineX - Adds horror and atmospheric effects to the server
 * Features: Horror events, scary sounds, blindness effects, jump scares
 */
public class HorrorEngineXPlugin extends JavaPlugin implements Listener {
    
    private final HorrorEventManager eventManager = new HorrorEventManager(this);
    private final HorrorEffectsManager effectsManager = new HorrorEffectsManager(this);
    private final CinematicEffectsManager cinematicManager = new CinematicEffectsManager(this);
    private final HorrorConfigManager configManager = new HorrorConfigManager(this);
    private final WorldGenManager worldGenManager = new WorldGenManager(this);
    private final Set<UUID> bypassedPlayers = new HashSet<>();
    
    @Override
    public void onEnable() {
        try {
            // Create default config
            saveDefaultConfig();
            
            // Load configuration
            configManager.loadConfig();
            
            // Register listeners
            getServer().getPluginManager().registerEvents(this, this);
            getServer().getPluginManager().registerEvents(eventManager, this);
            getServer().getPluginManager().registerEvents(effectsManager, this);
            getServer().getPluginManager().registerEvents(cinematicManager, this);
            getServer().getPluginManager().registerEvents(worldGenManager, this);
            
            // Register commands
            getCommand("horrorenginex").setExecutor(new HorrorEngineXCommand(this));
            
            // Start horror event scheduler
            eventManager.startEventScheduler();
            
            // Start world generation
            worldGenManager.startWorldGeneration();
            
            getLogger().info("═══════════════════════════════════════════════════════");
            getLogger().info("👻 HorrorEngineX enabled!");
            getLogger().info("Horror events: " + (configManager.isHorrorEventsEnabled() ? "§aON" : "§cOFF"));
            getLogger().info("Sound effects: " + (configManager.isSoundEffectsEnabled() ? "§aON" : "§cOFF"));
            getLogger().info("Atmospheric effects: " + (configManager.isAtmosphericEffectsEnabled() ? "§aON" : "§cOFF"));
            getLogger().info("═══════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            getLogger().severe("Failed to initialize HorrorEngineX: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        if (eventManager != null) {
            eventManager.stopEventScheduler();
        }
        if (worldGenManager != null) {
            worldGenManager.stopWorldGeneration();
        }
        getLogger().info("👻 HorrorEngineX disabled!");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!canBeAffected(player)) {
            return;
        }
        
        // Welcome message
        if (configManager.isJoinMessageEnabled()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                player.sendMessage("§6§lWelcome to the horror realm!");
                player.sendMessage("§c§oStay vigilant... §3You never know what's lurking...");
            }, 20);
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
            event.getFrom().getBlockY() != event.getTo().getBlockY() ||
            event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            
            Player player = event.getPlayer();
            
            if (!canBeAffected(player)) {
                return;
            }
            
            // Random horror events
            if (configManager.isHorrorEventsEnabled() && Math.random() < 0.001) {
                effectsManager.triggerRandomHorrorEffect(player);
            }
        }
    }
    
    /**
     * Check if player can be affected by horror effects
     */
    private boolean canBeAffected(Player player) {
        return !bypassedPlayers.contains(player.getUniqueId()) && 
               player.hasPermission("horrorenginex.use") &&
               !player.hasPermission("horrorenginex.bypass");
    }
    
    /**
     * Toggle horror bypass for a player
     */
    public void toggleBypass(Player player) {
        if (bypassedPlayers.contains(player.getUniqueId())) {
            bypassedPlayers.remove(player.getUniqueId());
            player.sendMessage("§a✓ Horror effects §aenabled");
        } else {
            bypassedPlayers.add(player.getUniqueId());
            player.sendMessage("§c✓ Horror effects §cdisabled");
        }
    }
    
    /**
     * Get the event manager
     */
    public HorrorEventManager getEventManager() {
        return eventManager;
    }
    
    /**
     * Get the effects manager
     */
    public HorrorEffectsManager getEffectsManager() {
        return effectsManager;
    }
    
    /**
     * Get the cinematic effects manager
     */
    public CinematicEffectsManager getCinematicManager() {
        return cinematicManager;
    }
    
    /**
     * Get the config manager
     */
    public HorrorConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Check if player is bypassed
     */
    public boolean isBypassed(Player player) {
        return bypassedPlayers.contains(player.getUniqueId());
    }
}
