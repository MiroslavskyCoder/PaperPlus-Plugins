package com.webx.friendfeed;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

public class FeedListener implements Listener {
    
    private final FriendFeedPlugin plugin;
    
    public FeedListener(FriendFeedPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player feeder = event.getPlayer();
        
        // Check if player is sneaking
        if (!feeder.isSneaking()) return;
        if (!plugin.isFriendFeedEnabled()) return;
        
        // Check if right-clicked entity is a player
        if (!(event.getRightClicked() instanceof Player)) return;
        
        Player target = (Player) event.getRightClicked();
        
        // Can't feed yourself
        if (feeder.equals(target)) return;
        
        // Check permission
        if (plugin.isRequirePermission() && !feeder.hasPermission(plugin.getPermission())) {
            feeder.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        
        // Check range
        if (feeder.getLocation().distance(target.getLocation()) > plugin.getRange()) {
            feeder.sendMessage(plugin.getMessage("too-far"));
            return;
        }
        
        // Check if player is full
        if (target.getFoodLevel() >= 20) {
            feeder.sendMessage(plugin.getMessage("player-full").replace("%player%", target.getName()));
            return;
        }
        
        // Check cooldown
        UUID feederUUID = feeder.getUniqueId();
        if (plugin.getCooldowns().containsKey(feederUUID)) {
            long timeLeft = (plugin.getCooldowns().get(feederUUID) - System.currentTimeMillis()) / 1000;
            if (timeLeft > 0) {
                feeder.sendMessage(plugin.getMessage("on-cooldown").replace("%time%", String.valueOf(timeLeft)));
                return;
            }
        }
        
        // Feed the target
        int currentFood = target.getFoodLevel();
        int newFood = Math.min(20, currentFood + plugin.getRestoreHunger());
        target.setFoodLevel(newFood);
        
        float currentSat = target.getSaturation();
        float newSat = Math.min(20, (float) (currentSat + plugin.getRestoreSaturation()));
        target.setSaturation(newSat);
        
        // Apply effects
        if (plugin.isGiveEffects()) {
            for (FriendFeedPlugin.EffectConfig effect : plugin.getEffects()) {
                target.addPotionEffect(new PotionEffect(
                    effect.getType(),
                    effect.getDuration(),
                    effect.getAmplifier()
                ));
            }
        }
        
        // Set cooldown
        plugin.getCooldowns().put(feederUUID, System.currentTimeMillis() + (plugin.getCooldown() * 1000L));
        
        // Send messages
        feeder.sendMessage(plugin.getMessage("fed-player").replace("%player%", target.getName()));
        target.sendMessage(plugin.getMessage("fed-by").replace("%feeder%", feeder.getName()));
        
        event.setCancelled(true);
    }
}
