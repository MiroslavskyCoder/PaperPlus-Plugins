package com.webx.abomination;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

public class AbominationListener implements Listener {
    private final AbominationPlugin plugin;

    public AbominationListener(AbominationPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (plugin.getManager().isMarked(event.getEntity()) && plugin.getManager().isFireImmune()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (!plugin.getManager().isMarked(e)) return;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (plugin.getManager().isFireImmune()) {
            switch (cause) {
                case FIRE:
                case FIRE_TICK:
                case HOT_FLOOR:
                case LAVA:
                    event.setCancelled(true);
                    return;
                default:
                    break;
            }
        }
        if (plugin.getManager().isExplosionImmune()) {
            switch (cause) {
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    event.setCancelled(true);
                    return;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (plugin.getManager().isAbomination(event.getEntity())) {
            String msg = plugin.getConfig().getString("messages.killed", "&aАбоминация повержена.");
            event.getEntity().getWorld().getPlayers().forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg)));
        }
    }

    @EventHandler
    public void onRetarget(EntityTargetEvent event) {
        if (plugin.getManager().isAbomination(event.getEntity())) {
            // ensure aggressive to players only
            if (!(event.getTarget() instanceof org.bukkit.entity.Player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // No-op hook reserved for future (e.g., warnings)
    }
}
