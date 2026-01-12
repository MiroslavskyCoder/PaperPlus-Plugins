package com.webx.regionigroks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ProtectionListener implements Listener {
    private final RegionigroksMapPlugin plugin;
    private final Map<UUID, Boolean> playerInSafeZone = new HashMap<>();

    public ProtectionListener(RegionigroksMapPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Optional<Region> r = plugin.getRegionManager().findRegionAt(loc);
        if (r.isPresent() && !r.get().isMember(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are not allowed to break blocks in region '" + r.get().getName() + "'.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Optional<Region> r = plugin.getRegionManager().findRegionAt(loc);
        if (r.isPresent() && !r.get().isMember(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are not allowed to place blocks in region '" + r.get().getName() + "'.");
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        if (!(event.getEntity() instanceof LivingEntity)) return; // only block mob killing
        Location loc = event.getEntity().getLocation();
        Optional<Region> r = plugin.getRegionManager().findRegionAt(loc);
        if (r.isPresent() && !r.get().isMember(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are not allowed to attack mobs in region '" + r.get().getName() + "'.");
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Prevent monster spawn in SafeZone
        if (!(event.getEntity() instanceof Monster)) return;
        
        Location loc = event.getLocation();
        Optional<Region> r = plugin.getRegionManager().findRegionAt(loc);
        
        // Cancel spawn if in SafeZone
        if (r.isPresent() && r.get().getName().equalsIgnoreCase("SafeZone")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Check if player moved to a different block
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || (from.getBlockX() == to.getBlockX() && 
                           from.getBlockY() == to.getBlockY() && 
                           from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        Optional<Region> regionAtTo = plugin.getRegionManager().findRegionAt(to);
        boolean nowInSafeZone = regionAtTo.isPresent() && 
                                regionAtTo.get().getName().equalsIgnoreCase("SafeZone");
        
        Boolean wasInSafeZone = playerInSafeZone.getOrDefault(playerId, false);
        
        // Player just entered SafeZone
        if (nowInSafeZone && !wasInSafeZone) {
            playerInSafeZone.put(playerId, true);
            
            // Show title
            Component title = Component.text("Безопасная Зона", NamedTextColor.GREEN);
            Component subtitle = Component.text("Вы находитесь под защитой", NamedTextColor.GRAY);
            
            Title titleDisplay = Title.title(
                title, 
                subtitle,
                Title.Times.times(
                    Duration.ofMillis(500),  // fade in
                    Duration.ofMillis(2000), // stay
                    Duration.ofMillis(500)   // fade out
                )
            );
            
            player.showTitle(titleDisplay);
            player.sendMessage(ChatColor.GREEN + "✓ Вы вошли в безопасную зону");
        }
        // Player left SafeZone
        else if (!nowInSafeZone && wasInSafeZone) {
            playerInSafeZone.put(playerId, false);
            player.sendMessage(ChatColor.YELLOW + "⚠ Вы покинули безопасную зону");
        }
    }
}

}