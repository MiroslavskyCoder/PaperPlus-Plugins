package com.webx.mobcatch;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MobCatchListener implements Listener {
    
    private final MobCatchPlugin plugin;
    
    public MobCatchListener(MobCatchPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        
        // Check if player is sneaking and right-clicking with empty hand
        if (!player.isSneaking()) return;
        if (!plugin.isEnabled()) return;
        
        // Check permission
        if (plugin.isRequirePermission() && !player.hasPermission(plugin.getPermission())) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        
        // Check if entity is a mob
        if (!(entity instanceof LivingEntity) || entity instanceof Player) return;
        
        EntityType type = entity.getType();
        
        // Check if mob is blacklisted
        if (plugin.getBlacklistedMobs().contains(type)) {
            player.sendMessage(plugin.getMessage("cannot-catch"));
            return;
        }
        
        // Check if mob is allowed
        if (!plugin.getAllowedMobs().isEmpty() && !plugin.getAllowedMobs().contains(type)) {
            player.sendMessage(plugin.getMessage("cannot-catch"));
            return;
        }
        
        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getMessage("inventory-full"));
            return;
        }
        
        // Create spawn egg
        Material eggMaterial = getSpawnEggMaterial(type);
        if (eggMaterial == null) {
            player.sendMessage(plugin.getMessage("cannot-catch"));
            return;
        }
        
        ItemStack egg = new ItemStack(eggMaterial);
        ItemMeta meta = egg.getItemMeta();
        
        // Set custom name
        String mobName = entity.getCustomName() != null ? entity.getCustomName() : type.name();
        meta.setDisplayName(plugin.getEggNameFormat().replace("%mob%", mobName));
        
        // Add lore
        List<String> lore = new ArrayList<>();
        lore.add("§7Type: §f" + type.name());
        if (entity.getCustomName() != null) {
            lore.add("§7Custom Name: §f" + entity.getCustomName());
        }
        meta.setLore(lore);
        
        egg.setItemMeta(meta);
        
        // Give egg and remove entity
        player.getInventory().addItem(egg);
        entity.remove();
        player.sendMessage(plugin.getMessage("caught"));
        
        event.setCancelled(true);
    }
    
    private Material getSpawnEggMaterial(EntityType type) {
        try {
            return Material.valueOf(type.name() + "_SPAWN_EGG");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
