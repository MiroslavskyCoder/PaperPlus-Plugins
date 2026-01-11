package com.webx.services;

import com.webx.PolyglotPlugin;
import com.webx.helper.SystemHelper;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SystemMonitorService {

    private final PolyglotPlugin plugin;
    private BukkitRunnable task;

    public SystemMonitorService(PolyglotPlugin plugin) {
        this.plugin = plugin;
    }

    public void startMonitoring() {
        // Запускаем задачу, которая выполняется каждые 2 секунды (40 тиков)
        task = new BukkitRunnable() {
            @Override
            public void run() {
                // if (!socket.connected()) return;

                // 1. Сбор данных
                long currentMemUsed = SystemHelper.getMemoryUsageMB();
                long maxMem = SystemHelper.getMaxMemoryMB();
                double cpuLoad = SystemHelper.getCpuLoad();
                long diskUsed = SystemHelper.getDiskUsageGB();
                long diskTotal = SystemHelper.getTotalDiskSpaceGB();
                int playerCount = Bukkit.getOnlinePlayers().size();

                // 2. Отправка через WebSocket
                // socket.emit("stats", gson.toJson(stats));
                plugin.getLogger().fine("Stats emitted: Memory=" + currentMemUsed + " Max=" + maxMem + 
                    " CPU=" + cpuLoad + "% Disk=" + diskUsed + "/" + diskTotal + "GB Players=" + playerCount);
            }
        };
        task.runTaskTimer(plugin, 0L, 40L); // 2 секунды
    }

    public void stopMonitoring() {
        if (task != null) {
            task.cancel();
        }
    }
}