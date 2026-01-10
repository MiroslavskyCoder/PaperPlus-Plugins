package com.webx.regionigroks;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.Location;

import java.util.Optional;

public class ProtectionListener implements Listener {
    private final RegionigroksMapPlugin plugin;

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
}
