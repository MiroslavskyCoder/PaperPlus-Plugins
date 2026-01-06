package com.webx.services;

import com.google.gson.Gson;
import com.webx.PolyglotPlugin;
import com.webx.helper.SystemHelper;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

public class SystemMonitorService {

    private final PolyglotPlugin plugin;
    private final Gson gson = new Gson();
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
                
                var stats = new Object() {
                    final String timestamp = new Date().toString();
                    final long memoryUsedMB = currentMemUsed;
                    final long memoryMaxMB = maxMem;
                    final double cpuLoadPercent = cpuLoad;
                    final long diskUsedGB = diskUsed;
                    final long diskTotalGB = diskTotal;
                    final int onlinePlayers = playerCount;
                };

                // 2. Отправка через Socket.IO
                // socket.emit("stats", gson.toJson(stats));
                plugin.getLogger().fine("Stats emitted.");
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