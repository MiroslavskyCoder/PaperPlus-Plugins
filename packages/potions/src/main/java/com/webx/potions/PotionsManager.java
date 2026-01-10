package com.webx.potions;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionsManager {
    
    public void applyBuff(Player player, String buffType, int duration) {
        switch (buffType.toLowerCase()) {
            case "strength":
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration * 20, 1));
                break;
            case "speed":
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, 1));
                break;
            case "haste":
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration * 20, 1));
                break;
            default:
                player.sendMessage("§cUnknown buff type!");
        }
    }
    
    public void removeBuffs(Player player) {
        player.getActivePotionEffects().forEach(effect -> 
            player.removePotionEffect(effect.getType())
        );
    }
}
