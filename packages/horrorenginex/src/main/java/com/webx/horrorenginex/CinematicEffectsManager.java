package com.webx.horrorenginex;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Cinematic horror effects manager - Visual rendering effects
 * Creates immersive horror atmosphere with visual artifacts
 */
public class CinematicEffectsManager implements Listener {
    
    private final HorrorEngineXPlugin plugin;
    private final Map<UUID, BossBar> cinematicBars = new HashMap<>();
    private final Map<UUID, BukkitTask> particleTasks = new HashMap<>();
    private final Random random = new Random();
    
    public CinematicEffectsManager(HorrorEngineXPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Apply full cinematic horror effect to player
     */
    public void applyCinematicEffect(Player player) {
        if (!plugin.getConfigManager().isAtmosphericEffectsEnabled()) {
            return;
        }
        
        // Apply black bars (cinematic effect)
        applyCinematicBars(player);
        
        // Apply atmospheric effects
        applyGrayAtmosphere(player);
        
        // Start dust/smoke particles
        startDustEffect(player);
        
        // Apply aura effect
        applyAuraEffect(player);
        
        // Make player feel watched
        applyWatchedEffect(player);
    }
    
    /**
     * Apply black cinematic bars (16:9 effect)
     */
    private void applyCinematicBars(Player player) {
        UUID playerId = player.getUniqueId();
        
        // Remove existing bar
        if (cinematicBars.containsKey(playerId)) {
            Bukkit.removeBossBar(cinematicBars.get(playerId).getKey());
        }
        
        // Create new boss bar (black bars effect)
        BossBar topBar = Bukkit.createBossBar(
            "§0█████████████████████████████████████████████████████",
            BarColor.BLACK,
            BarStyle.SOLID
        );
        topBar.setProgress(1.0);
        topBar.addPlayer(player);
        cinematicBars.put(playerId, topBar);
    }
    
    /**
     * Apply gray atmosphere effect
     */
    private void applyGrayAtmosphere(Player player) {
        // Apply blindness for gray vision
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.BLINDNESS, 1000, 0, false, false),
            PotionEffect.DurationApplied.EXTEND
        );
        
        // Apply slowness for heavy atmosphere
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.SLOWNESS, 1000, 1, false, false),
            PotionEffect.DurationApplied.EXTEND
        );
    }
    
    /**
     * Start infinite dust/smoke particle effect
     */
    private void startDustEffect(Player player) {
        UUID playerId = player.getUniqueId();
        
        // Cancel existing task
        if (particleTasks.containsKey(playerId)) {
            particleTasks.get(playerId).cancel();
        }
        
        // Start new particle task
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) return;
            
            Location loc = player.getLocation();
            
            // Gray dust clouds
            for (int i = 0; i < 10; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 3;
                double offsetY = random.nextDouble() * 2;
                double offsetZ = (random.nextDouble() - 0.5) * 3;
                
                Location dustLoc = loc.clone().add(offsetX, offsetY, offsetZ);
                
                // Gray particle cloud
                player.getWorld().spawnParticle(
                    Particle.SMOKE,
                    dustLoc,
                    5,
                    0.1, 0.1, 0.1,
                    0.02
                );
            }
            
            // Dark mist
            for (int i = 0; i < 5; i++) {
                double angle = random.nextDouble() * 2 * Math.PI;
                double distance = 2;
                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;
                
                Location mistLoc = loc.clone().add(offsetX, 1, offsetZ);
                
                player.getWorld().spawnParticle(
                    Particle.SPELL_MOB,
                    mistLoc,
                    3,
                    0.3, 0.3, 0.3,
                    0.01
                );
            }
        }, 0, 5);
        
        particleTasks.put(playerId, task);
    }
    
    /**
     * Apply aura effect around player
     */
    private void applyAuraEffect(Player player) {
        Location loc = player.getLocation();
        
        // Red aura particles (menacing)
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 8) {
            double x = Math.cos(angle) * 2;
            double z = Math.sin(angle) * 2;
            
            Location auraPt = loc.clone().add(x, 0.5, z);
            
            player.getWorld().spawnParticle(
                Particle.REDSTONE,
                auraPt,
                1,
                new Particle.DustOptions(Color.RED, 1.0f)
            );
        }
        
        // Glowing effect
        player.getWorld().spawnParticle(
            Particle.GLOW_SQUID_INK,
            loc.clone().add(0, 1.5, 0),
            5,
            0.5, 0.5, 0.5,
            0.05
        );
    }
    
    /**
     * Apply "being watched" effect
     */
    private void applyWatchedEffect(Player player) {
        // Random creepy messages
        String[] messages = {
            "§4§oКто-то смотрит на тебя...",
            "§c§oТы не один...",
            "§4§lОНИ ВИДЯТ ТЕБЯ",
            "§c§oВзгляд следит за каждым движением...",
            "§4§oПовернись... медленнее..."
        };
        
        String msg = messages[random.nextInt(messages.length)];
        player.sendActionBar(msg);
        
        // Apply audio cue
        plugin.getEffectsManager().playScareSound(player);
    }
    
    /**
     * Apply screen glitch effect
     */
    public void applyGlitchEffect(Player player) {
        // Rapid nausea for screen distortion
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.NAUSEA, 40, 2, false, false)
        );
        
        // Screen flickering effect using boss bar
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            BossBar bar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
            bar.setProgress(random.nextDouble());
            bar.addPlayer(player);
            
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Bukkit.removeBossBar(bar.getKey());
            }, 3);
        }, 0, 10);
    }
    
    /**
     * Apply entity glitch (visual bugs)
     */
    public void applyEntityGlitch(Player player) {
        // Vision distortion
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.DARKNESS, 100, 0, false, false)
        );
        
        // Slowness to make movement feel buggy
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.SLOWNESS, 100, 2, false, false)
        );
        
        // Weakness effect
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.WEAKNESS, 100, 1, false, false)
        );
    }
    
    /**
     * Trigger random glitch effect
     */
    public void triggerRandomGlitch(Player player) {
        int glitch = random.nextInt(4);
        
        switch (glitch) {
            case 0:
                applyGlitchEffect(player);
                break;
            case 1:
                applyEntityGlitch(player);
                break;
            case 2:
                triggerScreenFlip(player);
                break;
            case 3:
                triggerSilentScreenGlitch(player);
                break;
        }
    }
    
    /**
     * Trigger screen flip effect
     */
    private void triggerScreenFlip(Player player) {
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.NAUSEA, 60, 2, false, false)
        );
        
        player.sendActionBar("§4§l⚠ ⟲ ГЛИТЧ ЭКРАНА ⟲ ⚠");
    }
    
    /**
     * Silent glitch (no effects, just wrongness)
     */
    private void triggerSilentScreenGlitch(Player player) {
        // Subtle unsettling effect
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.SLOWNESS, 80, 0, false, false)
        );
        
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, false, false)
        );
    }
    
    /**
     * Remove cinematic effect from player
     */
    public void removeCinematicEffect(Player player) {
        UUID playerId = player.getUniqueId();
        
        // Remove boss bar
        if (cinematicBars.containsKey(playerId)) {
            Bukkit.removeBossBar(cinematicBars.get(playerId).getKey());
            cinematicBars.remove(playerId);
        }
        
        // Cancel particle task
        if (particleTasks.containsKey(playerId)) {
            particleTasks.get(playerId).cancel();
            particleTasks.remove(playerId);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize cinematic effect for new players
        if (plugin.getConfigManager().isAtmosphericEffectsEnabled()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                applyCinematicEffect(event.getPlayer());
            }, 20);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeCinematicEffect(event.getPlayer());
    }
}
