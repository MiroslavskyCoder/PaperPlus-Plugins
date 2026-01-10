package com.webx.afk.tasks;

import com.webx.afk.AFKPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AFKKickTask extends BukkitRunnable {
    private final AFKPlugin plugin;
    private final long kickAfterMillis;
    
    public AFKKickTask(AFKPlugin plugin, long kickAfterMillis) {
        this.plugin = plugin;
        this.kickAfterMillis = kickAfterMillis;
    }
    
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            var afkPlayer = plugin.getAFKManager().getAFKPlayers().get(player.getUniqueId());
            if (afkPlayer != null && afkPlayer.isAFK() && afkPlayer.getAFKDuration() > kickAfterMillis) {
                player.kickPlayer("§cKicked for being AFK too long!");
            }
        }
    }
}
