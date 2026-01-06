package com.webx.core;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;

public class Server {

    private String name;
    private String address;
    private int port;
    private String version;

    private Javalin app;
    private Set<WsContext> clients = new CopyOnWriteArraySet<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private JavaPlugin plugin;

    public Server() { }

    public void setFromPluginAllow(JavaPlugin plugin) {
        this.plugin = plugin;
        this.name = plugin.getServer().getName();
        this.address = plugin.getServer().getIp();
        this.port = plugin.getServer().getPort();
        this.version = plugin.getServer().getMinecraftVersion();

        plugin.getLogger().info("Server info set from plugin:");
        plugin.getLogger().info("Name: " + name);
        plugin.getLogger().info("Address: " + address);
        plugin.getLogger().info("Port: " + port);
        plugin.getLogger().info("Version: " + version);

        // Инициализация Javalin сервера
        startWebServer();
    }

    private void startWebServer() {
        app = Javalin.create(config -> {
            config.staticFiles.add("/web");  // Обслуживание статических файлов из /web
        }).start(8080);

        // WebSocket для метрик
        app.ws("/metrics", ws -> {
            plugin.getLogger().info("WebSocket /metrics endpoint initialized"); 
            ws.onConnect(ctx -> {
                clients.add(ctx);
                plugin.getLogger().info("WebSocket client connected: " + ctx.sessionId());
            });
            ws.onClose(ctx -> {
                clients.remove(ctx);
                plugin.getLogger().info("WebSocket client disconnected: " + ctx.sessionId());
            });
            ws.onError(ctx -> plugin.getLogger().warning("WebSocket error: " + ctx.error()));
        });
 
        new BukkitRunnable() {
            @Override
            public void run() {
                collectAndSendMetrics();
            }
        }.runTaskTimer(plugin, 0L, 40L); 

        plugin.getLogger().info("Javalin WebSocket server started on port 8080");
    }

    private void collectAndSendMetrics() {
        try {
            // Сбор метрик
            long timestamp = System.currentTimeMillis();
            double cpuUsage = getCpuUsage();
            double memoryUsage = getMemoryUsage();
            int onlinePlayers = plugin.getServer().getOnlinePlayers().size();

            // Форматирование в JSON
            String json = objectMapper.writeValueAsString(new MetricsData(timestamp, cpuUsage, memoryUsage, onlinePlayers));

            // Отправка всем клиентам
            for (WsContext client : clients) {
                client.send(json);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error collecting/sending metrics: " + e.getMessage());
        }
    }

    // Простая оценка CPU (заглушка, используйте ManagementFactory для точности)
    private double getCpuUsage() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Вызов GC для более точной оценки
        long startTime = System.nanoTime();
        runtime.gc();
        long endTime = System.nanoTime();
        return ((double) (endTime - startTime) / 1_000_000_000) * 100;
    }
 
    private double getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        return ((double) (totalMemory - freeMemory) / totalMemory) * 100;
    }

    public void stopWebServer() {
        if (app != null) {
            app.stop();
            plugin.getLogger().info("Javalin server stopped");
        }
    }

    // Геттеры
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getPort() { return port; }
    public String getVersion() { return version; }

    // Класс для данных метрик
    public static class MetricsData {
        public long timestamp;
        public double cpuUsage;
        public double memoryUsage;
        public int onlinePlayers;

        public MetricsData(long timestamp, double cpuUsage, double memoryUsage, int onlinePlayers) {
            this.timestamp = timestamp;
            this.cpuUsage = cpuUsage;
            this.memoryUsage = memoryUsage;
            this.onlinePlayers = onlinePlayers;
        }
    }
}
