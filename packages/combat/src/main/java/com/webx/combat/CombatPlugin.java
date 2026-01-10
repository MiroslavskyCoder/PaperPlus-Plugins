package com.webx.combat;

import com.webx.combat.listeners.MobKillRewardListener;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register mob kill reward listener
        getServer().getPluginManager().registerEvents(new MobKillRewardListener(), this);
        
        getLogger().info("Combat plugin enabled!");
        getLogger().info("Mob kill rewards: +1 coin per kill");
    }

    @Override
    public void onDisable() {
        getLogger().info("Combat plugin disabled!");
    }
}
