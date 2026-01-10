package com.webx.clans.listeners;

import com.webx.clans.ClansPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {
    private final ClansPlugin plugin;

    public EntityDamageListener(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim) || 
            !(event.getDamager() instanceof Player attacker)) {
            return;
        }

        var victimClan = plugin.getClanManager().getClanByMember(victim.getUniqueId());
        var attackerClan = plugin.getClanManager().getClanByMember(attacker.getUniqueId());

        if (victimClan != null && victimClan.equals(attackerClan)) {
            event.setCancelled(true);
            attacker.sendMessage("§cYou can't damage clan members!");
        }
    }
}
