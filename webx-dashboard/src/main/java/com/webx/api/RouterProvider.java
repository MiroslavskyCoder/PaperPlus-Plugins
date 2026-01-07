package com.webx.api;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webx.api.models.SettingsConfig;
import com.webx.api.models.SettingsConfig.RedisConfig;
import com.webx.api.models.SettingsConfig.SQLConfig;
import com.webx.api.services.CurseForgeService;
import com.webx.api.services.MetricsService;
import com.webx.api.services.PlayerService;
import com.webx.api.services.PluginService;
import com.webx.helper.SystemHelper;
import com.webx.services.SettingsService;

import io.javalin.Javalin;

public class RouterProvider { 

    private Javalin app; 
    private ObjectMapper objectMapper = new ObjectMapper();
    private JavaPlugin plugin;
    private SettingsService settingsService;
    private MetricsService metricsService;
    private PlayerService playerService;
    private PluginService pluginService;
    private CurseForgeService curseForgeService;

    public RouterProvider(JavaPlugin plugin) {
        this.plugin = plugin;
        this.settingsService = new SettingsService(plugin);
        this.metricsService = new MetricsService(plugin);
        this.playerService = new PlayerService(plugin);
        this.pluginService = new PluginService(plugin);
        this.curseForgeService = new CurseForgeService(plugin, settingsService);
        
        this.startWebServer();
        this.registerRoutes();
    }
    
    private void registerRoutes() {
        // ===== WEBSOCKET ENDPOINTS =====
        registerMetricsWebSocket();
        registerPlayersMetricsWebSocket();
        
        // ===== PLAYER ENDPOINTS =====
        registerPlayerRoutes();
        
        // ===== SERVER ENDPOINTS =====
        registerServerRoutes();
        
        // ===== PLUGIN ENDPOINTS =====
        registerPluginRoutes();
        
        // ===== CURSEFORGE ENDPOINTS =====
        registerCurseForgeRoutes();
        
        // ===== SETTINGS ENDPOINTS =====
        registerSettingsRoutes();
    }
    
    private void registerMetricsWebSocket() {
        app.ws("/metrics", ws -> {
            plugin.getLogger().info("WebSocket /metrics endpoint initialized"); 
            ws.onConnect(ctx -> {
                metricsService.getMetricsClients().add(ctx);
                plugin.getLogger().info("✅ WebSocket client connected: " + ctx.sessionId() + " | Total clients: " + metricsService.getMetricsClients().size());
            });
            ws.onClose(ctx -> {
                metricsService.getMetricsClients().remove(ctx);
                plugin.getLogger().info("❌ WebSocket client disconnected: " + ctx.sessionId() + " | Total clients: " + metricsService.getMetricsClients().size());
            });
            ws.onError(ctx -> {
                Throwable err = ctx.error();
                if (err instanceof java.nio.channels.ClosedChannelException) {
                    plugin.getLogger().info("WS closed (metrics): " + ctx.sessionId());
                } else {
                    plugin.getLogger().warning("⚠️ WebSocket error (metrics) for " + ctx.sessionId() + ": " + err);
                }
                metricsService.getMetricsClients().remove(ctx);
            });
        });
    }
    
    private void registerPlayersMetricsWebSocket() {
        app.ws("/players/metrics", ws -> {
            plugin.getLogger().info("🎮 WebSocket /players/metrics endpoint initialized");
            ws.onConnect(ctx -> {
                metricsService.getPlayersClients().add(ctx);
                plugin.getLogger().info("✅ Players WebSocket connected: " + ctx.sessionId() + " | Total: " + metricsService.getPlayersClients().size());
                metricsService.sendPlayersMetrics();
            });
            ws.onClose(ctx -> {
                metricsService.getPlayersClients().remove(ctx);
                plugin.getLogger().info("❌ Players WebSocket disconnected: " + ctx.sessionId() + " | Total: " + metricsService.getPlayersClients().size());
            });
            ws.onError(ctx -> {
                Throwable err = ctx.error();
                if (err instanceof java.nio.channels.ClosedChannelException) {
                    plugin.getLogger().info("WS closed (players): " + ctx.sessionId());
                } else {
                    plugin.getLogger().warning("⚠️ Players WebSocket error: " + err);
                }
                metricsService.getPlayersClients().remove(ctx);
            });
        });
    }
    
    private void registerPlayerRoutes() {
        app.get(API.getFullPath("players"), handler -> {
            plugin.getLogger().info("📊 GET /api/players request received");
            handler.json(plugin.getServer().getOnlinePlayers().stream().map(player -> {
                return new com.webx.player.PlayerProfile(player);
            }).toArray());
            plugin.getLogger().info("✅ GET /api/players response sent");
        });

        app.post(API.getFullPath("players/{uuid}/kick"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                String reason = handler.queryParam("reason");
                playerService.kickPlayer(playerUuid, reason);
                handler.json(new Object() { public boolean success = true; public String message = "Player kick scheduled"; });
            } catch (Exception e) {
                plugin.getLogger().warning("Error kicking player: " + e.getMessage());
                handler.status(400).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("players/{uuid}/ban"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                String reason = handler.queryParam("reason");
                playerService.banPlayer(playerUuid, reason);
                handler.json(new Object() { public boolean success = true; public String message = "Player ban scheduled"; });
            } catch (Exception e) {
                plugin.getLogger().warning("Error banning player: " + e.getMessage());
                handler.status(400).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("players/{uuid}/teleport"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                playerService.teleportPlayer(playerUuid);
                handler.json(new Object() { public boolean success = true; public String message = "Player teleport scheduled"; });
            } catch (Exception e) {
                plugin.getLogger().warning("Error teleporting player: " + e.getMessage());
                handler.status(400).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("players/{uuid}/heal"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                playerService.healPlayer(playerUuid);
                handler.json(new Object() { public boolean success = true; public String message = "Player heal scheduled"; });
            } catch (Exception e) {
                plugin.getLogger().warning("Error healing player: " + e.getMessage());
                handler.status(400).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });
    }

    
    private void registerServerRoutes() {
        app.get(API.getFullPath("server/status"), handler -> {
            plugin.getLogger().info("📊 GET /api/server/status request received");
            try {
                Runtime runtime = Runtime.getRuntime();
                long memUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
                long memMax = runtime.maxMemory() / (1024 * 1024);
                double cpuLoad = SystemHelper.getCpuLoad() * 100;
                
                Object status = new Object() {
                    public String name = plugin.getServer().getName();
                    public String version = plugin.getServer().getVersion();
                    public int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
                    public int maxPlayers = plugin.getServer().getMaxPlayers();
                    public double cpuUsage = cpuLoad;
                    public long memoryUsed = memUsed;
                    public long memoryMax = memMax;
                    public String motd = plugin.getServer().getMotd();
                    public boolean online = true;
                    public long uptime = System.currentTimeMillis();
                    public String gameMode = "Survival";
                    public int difficulty = 2;
                    public boolean pvp = true;
                };
                
                handler.json(status);
                plugin.getLogger().info("✅ GET /api/server/status response sent");
            } catch (Exception e) {
                plugin.getLogger().warning("❌ Error fetching server status: " + e.getMessage());
                handler.status(500).result("Error fetching server status");
            }
        });
    }
    
    private void registerPluginRoutes() {
        app.get(API.getFullPath("plugins"), handler -> {
            handler.json(pluginService.getPlugins());
        });

        app.post(API.getFullPath("plugins/{name}/enable"), handler -> {
            String name = handler.pathParam("name");
            try {
                pluginService.enablePlugin(name);
                handler.json(new Object() { public boolean success = true; public String message = "Enable scheduled"; });
            } catch (Exception e) {
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("plugins/{name}/disable"), handler -> {
            String name = handler.pathParam("name");
            try {
                pluginService.disablePlugin(name);
                handler.json(new Object() { public boolean success = true; public String message = "Disable scheduled"; });
            } catch (Exception e) {
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("setconfig"), handler -> {
            String pluginId = handler.formParam("pluginId");
            String key = handler.formParam("key");
            String value = handler.formParam("value"); 

            org.bukkit.plugin.Plugin targetPlugin = plugin.getServer().getPluginManager().getPlugin(pluginId);

            if (targetPlugin != null) {
                targetPlugin.getConfig().set(key, value);
                targetPlugin.saveConfig(); 
                handler.result("Configuration updated successfully.");
            } else {
                handler.status(404).result("Plugin not found.");
            }
        });
    }
    
    private void registerCurseForgeRoutes() {
        app.get(API.getFullPath("curseforge/search"), handler -> {
            try {
                String q = handler.queryParam("q");
                String gameId = handler.queryParam("gameId");
                String classId = handler.queryParam("classId");
                
                String response = curseForgeService.searchMods(q, gameId, classId);
                handler.contentType("application/json").result(response);
            } catch (Exception e) {
                plugin.getLogger().warning("Error proxying CurseForge search: " + e.getMessage());
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        app.get(API.getFullPath("curseforge/mods/{modId}/files"), handler -> {
            try {
                String modId = handler.pathParam("modId");
                String response = curseForgeService.getModFiles(modId);
                handler.contentType("application/json").result(response);
            } catch (Exception e) {
                plugin.getLogger().warning("Error fetching mod files: " + e.getMessage());
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("curseforge/install"), handler -> {
            try {
                String modId = handler.queryParam("modId");
                String fileId = handler.queryParam("fileId");
                
                if (modId == null || fileId == null) {
                    handler.status(400).json(new Object() { public boolean success = false; public String message = "Missing modId or fileId"; });
                    return;
                }

                String fileInfoJson = curseForgeService.getFileInfo(modId, fileId);
                String downloadUrl = curseForgeService.extractJsonField(fileInfoJson, "downloadUrl");
                String fileName = curseForgeService.extractJsonField(fileInfoJson, "fileName");
                
                if (downloadUrl == null || fileName == null) {
                    handler.status(500).json(new Object() { public boolean success = false; public String message = "Failed to parse file info"; });
                    return;
                }

                curseForgeService.installMod(modId, fileId, fileName, downloadUrl);
                handler.json(new Object() { 
                    public boolean success = true; 
                    public String message = "Installed to /plugins"; 
                    public String path = new File(plugin.getDataFolder().getParentFile(), fileName).getAbsolutePath(); 
                });
            } catch (Exception e) {
                plugin.getLogger().warning("Error installing plugin: " + e.getMessage());
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });
    }
    
    private void registerSettingsRoutes() {
        app.get(API.getFullPath("settings"), handler -> {
            plugin.getLogger().info("📊 GET /api/settings request received");
            handler.json(settingsService.getSettings());
            plugin.getLogger().info("✅ GET /api/settings response sent");
        });

        app.put(API.getFullPath("settings"), handler -> {
            plugin.getLogger().info("📝 PUT /api/settings request received");
            try {
                SettingsConfig updatedConfig = objectMapper.readValue(
                    handler.body(), 
                    SettingsConfig.class
                );
                settingsService.updateSettings(updatedConfig);
                handler.json(updatedConfig);
                plugin.getLogger().info("✅ PUT /api/settings - Settings updated successfully");
            } catch (Exception e) {
                plugin.getLogger().warning("❌ Error updating settings: " + e.getMessage());
                e.printStackTrace();
                handler.status(400).result("Invalid settings format");
            }
        });

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

    // Helper classes
    public static class TestConnectionRequest {
        public String type;
        public SettingsConfig config;
    }

    public static class TestConnectionResponse {
        public boolean success;
        
        public TestConnectionResponse(boolean success) {
            this.success = success;
        }
    }

    private void startWebServer() {
        app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/web";
                staticFiles.location = io.javalin.http.staticfiles.Location.CLASSPATH;
                staticFiles.precompress = false;
                staticFiles.aliasCheck = null;
            });
            
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
            
            config.requestLogger.http((ctx, ms) -> {
                plugin.getLogger().info(String.format("[HTTP] %s %s - %d (%s ms)",
                    ctx.method(),
                    ctx.path(),
                    ctx.status().getCode(),
                    ms
                ));
            });
            
            config.requestLogger.ws(ws -> {
                ws.onConnect(ctx -> 
                    plugin.getLogger().info("[WS] Connected: " + ctx.sessionId())
                );
                ws.onClose(ctx -> 
                    plugin.getLogger().info("[WS] Closed: " + ctx.sessionId())
                );
                ws.onError(ctx -> 
                    plugin.getLogger().info("[WS] Error: " + ctx.error())
                );
            });
            
        }).start(9092);

        plugin.getLogger().info("====================================");
        plugin.getLogger().info("Javalin WebSocket server started!");
        plugin.getLogger().info("Port: 9092");
        plugin.getLogger().info("WebSocket: ws://localhost:9092/metrics");
        plugin.getLogger().info("REST API: http://localhost:9092/api/*");
        plugin.getLogger().info("Static files: http://localhost:9092/");
        plugin.getLogger().info("====================================");

        new BukkitRunnable() {
            @Override
            public void run() {
                metricsService.collectAndSendMetrics();
            }
        }.runTaskTimer(plugin, 0L, 40L);

        new BukkitRunnable() {
            @Override
            public void run() {
                metricsService.sendPlayersMetrics();
            }
        }.runTaskTimer(plugin, 0L, 20L);

        plugin.getLogger().info("Metrics collection started (every 2 seconds)");
        plugin.getLogger().info("Players metrics started (every 1 second)");
    }

    public void stopWebServer() {
        if (app != null) {
            app.stop();
            plugin.getLogger().info("Javalin server stopped");
        }
    }
}
