package com.webx.api;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webx.core.Server.MetricsData;
import com.webx.helper.SystemHelper;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

public class RouterProvider { 

    private Javalin app; 
    private Set<WsContext> clients = new CopyOnWriteArraySet<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private JavaPlugin plugin;

    public RouterProvider(JavaPlugin plugin) {
        
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
    }

    private void collectAndSendMetrics() {
        try {
            // Сбор метрик
            long timestamp = System.currentTimeMillis();
            double cpuUsage = SystemHelper.getCpuLoad();
            double memoryUsage = SystemHelper.getMemoryUsageMB();
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

    private void startWebServer() {
        app = Javalin.create(config -> {
            config.staticFiles.add("/web");  // Обслуживание статических файлов из /web
        }).start(8080);

        new BukkitRunnable() {
            @Override
            public void run() {
                collectAndSendMetrics();
            }
        }.runTaskTimer(plugin, 0L, 40L); 

        plugin.getLogger().info("Javalin WebSocket server started on port 8080");
    }

    public void stopWebServer() {
        if (app != null) {
            app.stop();
            plugin.getLogger().info("Javalin server stopped");
        }
    }
}
