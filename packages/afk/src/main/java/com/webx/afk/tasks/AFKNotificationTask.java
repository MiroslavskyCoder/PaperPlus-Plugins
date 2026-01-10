package com.webx.afk.tasks;

import com.webx.afk.AFKPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AFKNotificationTask extends BukkitRunnable {
    private final AFKPlugin plugin;
    
    public AFKNotificationTask(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        // Notify players about AFK status
    }
}
