package com.webx.api.services;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.plugin.java.JavaPlugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webx.core.Server.MetricsData;
import com.webx.helper.SystemHelper;

import io.javalin.websocket.WsContext;

public class MetricsService {
    
    private final JavaPlugin plugin;
    private final ObjectMapper objectMapper;
    private final Set<WsContext> clients = new CopyOnWriteArraySet<>();
    private final Set<WsContext> playersClients = new CopyOnWriteArraySet<>();
    
    public MetricsService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.objectMapper = new ObjectMapper();
    }
    
    public Set<WsContext> getMetricsClients() {
        return clients;
    }
    
    public Set<WsContext> getPlayersClients() {
        return playersClients;
    }
    
    public void collectAndSendMetrics() {
        try {
            long timestamp = System.currentTimeMillis();
            double cpuLoad = SystemHelper.getCpuLoad();
            double cpuUsage = cpuLoad > 100 ? Math.min(cpuLoad / Runtime.getRuntime().availableProcessors(), 100) : 
                              cpuLoad <= 1 ? cpuLoad * 100 : cpuLoad;
            
            Runtime runtime = Runtime.getRuntime();
            long memUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long memMax = runtime.maxMemory() / (1024 * 1024);
            double memoryUsagePercent = (memUsed / (double) memMax) * 100;
            
            int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
            
            long diskUsed = 0;
            long diskTotal = 0;
            try {
                java.nio.file.FileStore store = java.nio.file.Files.getFileStore(java.nio.file.Paths.get("/"));
                diskUsed = (store.getTotalSpace() - store.getUsableSpace()) / (1024L * 1024L * 1024L);
                diskTotal = store.getTotalSpace() / (1024L * 1024L * 1024L);
            } catch (Exception e) {
                diskUsed = 0;
                diskTotal = 100;
            }
            
            MetricsData metrics = new MetricsData(
                timestamp,
                cpuUsage,
                memoryUsagePercent,
                onlinePlayers,
                memUsed,
                memMax,
                diskUsed,
                diskTotal
            );

            String json = objectMapper.writeValueAsString(metrics);

            if (!clients.isEmpty()) {
                for (WsContext client : clients) {
                    try {
                        client.send(json);
                    } catch (Exception e) {
                        plugin.getLogger().info("WS send failed (metrics), removing client " + client.sessionId());
                        clients.remove(client);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("❌ Error collecting/sending metrics: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendPlayersMetrics() {
        try {
            Object[] playersData = plugin.getServer().getOnlinePlayers().stream().map(player -> {
                return new Object() {
                    @SuppressWarnings("unused")
                    public String name = player.getName();
                    @SuppressWarnings("unused")
                    public String uuid = player.getUniqueId().toString();
                    @SuppressWarnings("unused")
                    public double health = player.getHealth();
                    @SuppressWarnings("unused")
                    public double maxHealth = 20.0;
                    @SuppressWarnings("unused")
                    public double x = player.getLocation().getX();
                    @SuppressWarnings("unused")
                    public double y = player.getLocation().getY();
                    @SuppressWarnings("unused")
                    public double z = player.getLocation().getZ();
                    @SuppressWarnings("unused")
                    public String world = player.getWorld().getName();
                    @SuppressWarnings("unused")
                    public int level = player.getLevel();
                    @SuppressWarnings("unused")
                    public float experience = player.getExp();
                    @SuppressWarnings("unused")
                    public int foodLevel = player.getFoodLevel();
                    @SuppressWarnings("unused")
                    public long ping = player.getPing();
                    @SuppressWarnings("unused")
                    public boolean online = player.isOnline();
                    @SuppressWarnings("unused")
                    public long timestamp = System.currentTimeMillis();
                };
            }).toArray();

            String json = objectMapper.writeValueAsString(playersData);

            if (!playersClients.isEmpty()) {
                for (WsContext client : playersClients) {
                    try {
                        client.send(json);
                    } catch (Exception e) {
                        plugin.getLogger().info("WS send failed (players), removing client " + client.sessionId());
                        playersClients.remove(client);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("❌ Error collecting/sending players: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
