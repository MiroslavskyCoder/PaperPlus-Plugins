package com.webx.afk.tasks;

import com.webx.afk.AFKPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AFKCheckTask extends BukkitRunnable {
    private final AFKPlugin plugin;
    
    public AFKCheckTask(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        plugin.getAFKManager().checkAFKStatus();
    }
}
