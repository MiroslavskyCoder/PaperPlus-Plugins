package com.webx.api;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webx.api.models.SettingsConfig;
import com.webx.api.models.SettingsConfig.RedisConfig;
import com.webx.api.models.SettingsConfig.SQLConfig;
import com.webx.core.Server.MetricsData;
import com.webx.helper.SystemHelper;
import com.webx.services.SettingsService;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

public class RouterProvider { 

    private Javalin app; 
    private Set<WsContext> clients = new CopyOnWriteArraySet<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private JavaPlugin plugin;
    private SettingsService settingsService;

    public RouterProvider(JavaPlugin plugin) {
        this.plugin = plugin;
        this.settingsService = new SettingsService(plugin);
        this.startWebServer();

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

        app.get(API.getFullPath("players"), handler -> {
            handler.json(plugin.getServer().getOnlinePlayers().stream().map(player -> {
                return new com.webx.player.PlayerProfile(player);
            }).toArray());
        });

        app.get(API.getFullPath("plugins"), handler -> {
            Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins(); 

            Object[] pluginProfiles = new Object[plugins.length];
            for (int i = 0; i < plugins.length; i++) {
                Plugin p = plugins[i];
                pluginProfiles[i] = new Object() {
                    public String name = p.getName();
                    public String version = p.getDescription().getVersion();
                    public boolean isEnabled = p.isEnabled();
                    public String description = p.getDescription().getDescription();
                    public String author = p.getDescription().getAuthors().get(0);
                    public String website = p.getDescription().getWebsite();  
                };
                
            }
            handler.json(pluginProfiles);
        });

        app.post(API.getFullPath("setconfig"), handler -> {
            String pluginId = handler.formParam("pluginId");
            String key = handler.formParam("key");
            String value = handler.formParam("value"); 

            Plugin targetPlugin = plugin.getServer().getPluginManager().getPlugin(pluginId);

            if (targetPlugin != null) {
                targetPlugin.getConfig().set(key, value);
                targetPlugin.saveConfig(); 
                handler.result("Configuration updated successfully.");
            } else {
                handler.status(404).result("Plugin not found.");
            }
        });

        // ===== SETTINGS API ENDPOINTS =====
        
        // GET /api/settings - Get all settings
        app.get(API.getFullPath("settings"), handler -> {
            handler.json(settingsService.getSettings());
        });

        // PUT /api/settings - Update settings
        app.put(API.getFullPath("settings"), handler -> {
            try {
                SettingsConfig updatedConfig = objectMapper.readValue(
                    handler.body(), 
                    SettingsConfig.class
                );
                settingsService.updateSettings(updatedConfig);
                handler.json(updatedConfig);
            } catch (Exception e) {
                plugin.getLogger().warning("Error updating settings: " + e.getMessage());
                handler.status(400).result("Invalid settings format");
            }
        });

        // POST /api/settings/test-connection - Test database and cache connections
        app.post(API.getFullPath("settings/test-connection"), handler -> {
            try {
                TestConnectionRequest request = objectMapper.readValue(
                    handler.body(),
                    TestConnectionRequest.class
                );
                
                boolean success = false;
                if ("sql".equals(request.type)) {
                    success = settingsService.testSQLConnection(request.config.sqlConfig);
                } else if ("redis".equals(request.type)) {
                    success = settingsService.testRedisConnection(request.config.redisConfig);
                }
                
                handler.json(new TestConnectionResponse(success));
            } catch (Exception e) {
                plugin.getLogger().warning("Error testing connection: " + e.getMessage());
                handler.status(400).json(new TestConnectionResponse(false));
            }
        });
    }

    // Helper classes for connection testing
    public static class TestConnectionRequest {
        public String type; // "sql" or "redis"
        public SettingsConfig config;
    }

    public static class TestConnectionResponse {
        public boolean success;
        
        public TestConnectionResponse(boolean success) {
            this.success = success;
        }
    }

    private void collectAndSendMetrics() {
        try {
            // Сбор полных метрик системы
            long timestamp = System.currentTimeMillis();
            double cpuUsage = SystemHelper.getCpuLoad() * 100; // Convert to percentage
            
            // Memory metrics
            Runtime runtime = Runtime.getRuntime();
            long memUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024); // MB
            long memMax = runtime.maxMemory() / (1024 * 1024); // MB
            double memoryUsagePercent = (memUsed / (double) memMax) * 100;
            
            // Players
            int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
            
            // Disk metrics (if available)
            long diskUsed = 0;
            long diskTotal = 0;
            try {
                java.nio.file.FileStore store = java.nio.file.Files.getFileStore(java.nio.file.Paths.get("/"));
                diskUsed = (store.getTotalSpace() - store.getUsableSpace()) / (1024L * 1024L * 1024L); // GB
                diskTotal = store.getTotalSpace() / (1024L * 1024L * 1024L); // GB
            } catch (Exception e) {
                diskUsed = 0;
                diskTotal = 100;
            }
            
            // Create comprehensive metrics data
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

            // Serialize to JSON
            String json = objectMapper.writeValueAsString(metrics);

            // Send to all connected clients
            if (!clients.isEmpty()) {
                for (WsContext client : clients) {
                    try {
                        client.send(json);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error sending metrics to client: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error collecting/sending metrics: " + e.getMessage());
        }
    }

    private void startWebServer() {
        app = Javalin.create(config -> {
            config.staticFiles.add("/web");  // Serve static files from /web
        }).start(9092); // Changed to 9092 to match frontend

        // Start metrics collection every 2 seconds (40 ticks = 2 seconds on 20 TPS server)
        new BukkitRunnable() {
            @Override
            public void run() {
                collectAndSendMetrics();
            }
        }.runTaskTimer(plugin, 0L, 40L); // 40 ticks = 2 seconds

        plugin.getLogger().info("Javalin WebSocket server started on port 9092");
        plugin.getLogger().info("Metrics will be sent every 2 seconds");
    }

    public void stopWebServer() {
        if (app != null) {
            app.stop();
            plugin.getLogger().info("Javalin server stopped");
        }
    }
}
