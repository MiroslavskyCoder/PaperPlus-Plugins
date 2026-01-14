package com.webx.hdphysicssound.listeners;

import com.webx.hdphysicssound.engine.SoundPhysicsEngine;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class SoundEventListener implements Listener {

    private final Plugin plugin;
    private final SoundPhysicsEngine engine;

    public SoundEventListener(Plugin plugin, SoundPhysicsEngine engine) {
        this.plugin = plugin;
        this.engine = engine;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        engine.broadcastPhysicalSound(loc, Sound.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Location loc = event.getEntity().getLocation().add(0, event.getEntity().getHeight() * 0.5, 0);
        engine.broadcastPhysicalSound(loc, Sound.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 0.8f, 1.0f);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        Location loc = event.getLocation();
        engine.broadcastPhysicalSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 1.5f, 0.9f);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        Location loc = event.getInteractionPoint() != null ? event.getInteractionPoint() : event.getClickedBlock().getLocation();
        engine.broadcastPhysicalSound(loc, Sound.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 0.6f, 1.0f);
    }
}
