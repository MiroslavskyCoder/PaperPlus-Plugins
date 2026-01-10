package com.webx.combat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;

public class CombatListener implements Listener {
    private CombatManager combatManager;
    
    public CombatListener(CombatManager manager) {
        this.combatManager = manager;
    }
    
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            
            combatManager.enterCombat(attacker);
            combatManager.enterCombat(victim);
        }
    }
}
