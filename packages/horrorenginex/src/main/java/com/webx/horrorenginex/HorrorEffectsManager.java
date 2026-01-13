package com.webx.horrorenginex;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * Manages horror effects for players
 */
public class HorrorEffectsManager implements Listener {
    
    private final HorrorEngineXPlugin plugin;
    private final Random random = new Random();
    
    public HorrorEffectsManager(HorrorEngineXPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Trigger a random horror effect on a player
     */
    public void triggerRandomHorrorEffect(Player player) {
        if (!plugin.getConfigManager().isAtmosphericEffectsEnabled()) {
            return;
        }
        
        int effectType = random.nextInt(6);
        
        switch (effectType) {
            case 0:
                applyBlindness(player);
                break;
            case 1:
                applySlowness(player);
                break;
            case 2:
                applyNausea(player);
                break;
            case 3:
                playHorrorSound(player);
                break;
            case 4:
                applyCreeperStare(player);
                break;
            case 5:
                applyDarkness(player);
                break;
        }
    }
    
    /**
     * Apply blindness effect
     */
    public void applyBlindness(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false));
        playEffectSound(player, Sound.ENTITY_CREEPER_PRIMED, 0.5f);
    }
    
    /**
     * Apply slowness effect
     */
    public void applySlowness(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1, false, false));
        playEffectSound(player, Sound.ENTITY_PHANTOM_AMBIENT, 0.7f);
    }
    
    /**
     * Apply nausea effect
     */
    public void applyNausea(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 80, 0, false, false));
        playEffectSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f);
    }
    
    /**
     * Apply darkness effect
     */
    public void applyDarkness(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 120, 0, false, false));
        playEffectSound(player, Sound.AMBIENT_CAVE, 0.8f);
    }
    
    /**
     * Apply creeper stare effect
     */
    public void applyCreeperStare(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 0, false, false));
        playEffectSound(player, Sound.ENTITY_CREEPER_DEATH, 1.0f);
        player.sendMessage("§4§l💀 Something is watching you...");
    }
    
    /**
     * Play horror sound effect
     */
    private void playHorrorSound(Player player) {
        Sound[] horrorSounds = {
            Sound.ENTITY_PHANTOM_AMBIENT,
            Sound.ENTITY_CREEPER_PRIMED,
            Sound.ENTITY_ENDERMAN_SCREAM,
            Sound.ENTITY_WITHER_SPAWN,
            Sound.ENTITY_WARDEN_AGITATED
        };
        
        Sound sound = horrorSounds[random.nextInt(horrorSounds.length)];
        playEffectSound(player, sound, 0.7f);
    }
    
    /**
     * Play scary sound to player
     */
    public void playScareSound(Player player) {
        Sound[] scareSounds = {
            Sound.ENTITY_PHANTOM_AMBIENT,
            Sound.ENTITY_ENDERMAN_SCREAM,
            Sound.ENTITY_WARDEN_AGITATED,
            Sound.ENTITY_WITHER_SPAWN,
            Sound.ENTITY_CREEPER_DEATH
        };
        
        Sound sound = scareSounds[random.nextInt(scareSounds.length)];
        playEffectSound(player, sound, 0.5f);
    }
    
    /**
     * Play sound effect to player
     */
    private void playEffectSound(Player player, Sound sound, float volume) {
        if (!plugin.getConfigManager().isSoundEffectsEnabled()) {
            return;
        }
        
        try {
            player.playSound(player.getLocation(), sound, volume, 1.0f);
        } catch (Exception e) {
            plugin.getLogger().fine("Failed to play sound: " + e.getMessage());
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Can add movement-based horror effects here
    }
}
