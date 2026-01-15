package com.webx.api;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.api.endpoints.AfkEndpoint;
import com.webx.api.endpoints.EconomyEndpoint;
import com.webx.api.endpoints.ShopEndpoint;
import com.webx.api.endpoints.PluginConfigEndpoint;
import com.webx.api.models.SettingsConfig;
import com.webx.api.services.*;
import com.webx.helper.SystemHelper;
import com.webx.services.SettingsService;

import io.javalin.Javalin;

public class RouterProvider { 

    private Javalin app; 
    private ObjectMapper objectMapper = new ObjectMapper();
    private Gson gson;
    private JavaPlugin plugin;
    private SettingsService settingsService;
    private MetricsService metricsService;
    private PlayerService playerService;
    private PluginService pluginService;
    private CurseForgeService curseForgeService;
    private EconomyEndpoint economyEndpoint;
    private ShopEndpoint shopEndpoint;
    private AfkEndpoint afkEndpoint;
    private PluginConfigEndpoint pluginConfigEndpoint;
    private ClanService clanService;
    private LeaderboardService leaderboardService;

    public RouterProvider(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.settingsService = new SettingsService(plugin);
        this.metricsService = new MetricsService(plugin);
        this.playerService = new PlayerService(plugin);
        this.pluginService = new PluginService(plugin);
        this.curseForgeService = new CurseForgeService(plugin, settingsService);
        this.economyEndpoint = new EconomyEndpoint(plugin);
        this.shopEndpoint = new ShopEndpoint(plugin, gson);
        this.afkEndpoint = new AfkEndpoint(plugin, gson);
        this.pluginConfigEndpoint = new PluginConfigEndpoint(plugin, gson);
        this.clanService = new ClanService();
        this.leaderboardService = new LeaderboardService(plugin);
        
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
        
        // ===== ECONOMY ENDPOINTS =====
        registerEconomyRoutes();
        
        // ===== SHOP CONFIG ENDPOINTS =====
        registerShopRoutes();
        
        // ===== AFK CONFIG ENDPOINTS =====
        registerAfkRoutes();
        
        // ===== NEW PLUGINS CONFIG ENDPOINTS =====
        registerPluginConfigRoutes();
        
        // ===== CLAN ENDPOINTS =====
        registerClanRoutes();
        
        // ===== LEADERBOARD ENDPOINTS =====
        registerLeaderboardRoutes();
        
        // ===== LOADERSCRIPT ENDPOINTS =====
        registerLoaderScriptRoutes();
    }
    
    private void registerMetricsWebSocket() {
        app.ws("/metrics", ws -> {
            plugin.getLogger().info("WebSocket /metrics endpoint initialized"); 
            ws.onConnect(ctx -> {
                metricsService.getMetricsClients().add(ctx);
                plugin.getLogger().info("✅ WebSocket client connected: #" + ctx.hashCode() + " | Total clients: " + metricsService.getMetricsClients().size());
            });
            ws.onClose(ctx -> {
                metricsService.getMetricsClients().remove(ctx);
                plugin.getLogger().info("❌ WebSocket client disconnected: #" + ctx.hashCode() + " | Total clients: " + metricsService.getMetricsClients().size());
            });
            ws.onError(ctx -> {
                Throwable err = ctx.error();
                if (err instanceof java.nio.channels.ClosedChannelException) {
                    plugin.getLogger().info("WS closed (metrics): #" + ctx.hashCode());
                } else {
                    plugin.getLogger().warning("⚠️ WebSocket error (metrics) for #" + ctx.hashCode() + ": " + err);
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
                handler.json(new Object() { 
                    @SuppressWarnings("unused")
                    public boolean success = true; 
                    @SuppressWarnings("unused")
                    public String message = "Player kick scheduled"; 
                });
            } catch (Exception e) {
                plugin.getLogger().warning("Error kicking player: " + e.getMessage());
                handler.status(400).json(new Object() { 
                    @SuppressWarnings("unused")
                    public boolean success = false; 
                    @SuppressWarnings("unused")
                    public String message = e.getMessage(); 
                });
            }
        });

        app.post(API.getFullPath("players/{uuid}/ban"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                String reason = handler.queryParam("reason");
                playerService.banPlayer(playerUuid, reason);
                handler.json(new Object() { 
                    @SuppressWarnings("unused")
                    public boolean success = true; 
                    @SuppressWarnings("unused")
                    public String message = "Player ban scheduled"; 
                });
            } catch (Exception e) {
                plugin.getLogger().warning("Error banning player: " + e.getMessage());
                handler.status(400).json(new Object() { 
                    @SuppressWarnings("unused")
                    public boolean success = false; 
                    @SuppressWarnings("unused")
                    public String message = e.getMessage(); 
                });
            }
        });

        app.post(API.getFullPath("players/{uuid}/teleport"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                playerService.teleportPlayer(playerUuid);
                handler.json(new Object() { 
                    @SuppressWarnings("unused")
                    public boolean success = true; 
                    @SuppressWarnings("unused")
                    public String message = "Player teleport scheduled"; 
                });
            } catch (Exception e) {
                plugin.getLogger().warning("Error teleporting player: " + e.getMessage());
                handler.status(400).json(new Object() { 
                    @SuppressWarnings("unused")
                    public boolean success = false; 
                    @SuppressWarnings("unused")
                    public String message = e.getMessage(); 
                });
            }
        });

        app.post(API.getFullPath("players/{uuid}/heal"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                playerService.healPlayer(playerUuid);
                handler.json(new Object() { 
                    @SuppressWarnings("unused")
                    public boolean success = true; 
                    @SuppressWarnings("unused")
                    public String message = "Player heal scheduled"; 
                });
            } catch (Exception e) {
                plugin.getLogger().warning("Error healing player: " + e.getMessage());
                handler.status(400).json(new Object() { 
                    @SuppressWarnings("unused")
                    public boolean success = false; 
                    @SuppressWarnings("unused")
                    public String message = e.getMessage(); 
                });
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
                    @SuppressWarnings("unused")
                    public String name = plugin.getServer().getName();
                    @SuppressWarnings("unused")
                    public String version = plugin.getServer().getVersion();
                    @SuppressWarnings("unused")
                    public int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
                    @SuppressWarnings("unused")
                    public int maxPlayers = plugin.getServer().getMaxPlayers();
                    @SuppressWarnings("unused")
                    public double cpuUsage = cpuLoad;
                    @SuppressWarnings("unused")
                    public long memoryUsed = memUsed;
                    @SuppressWarnings("unused")
                    public long memoryMax = memMax;
                    @SuppressWarnings({"deprecation", "unused"})
                    public String motd = plugin.getServer().getMotd();
                    @SuppressWarnings("unused")
                    public boolean online = true;
                    @SuppressWarnings("unused")
                    public long uptime = System.currentTimeMillis();
                    @SuppressWarnings("unused")
                    public String gameMode = "Survival";
                    @SuppressWarnings("unused")
                    public int difficulty = 2;
                    @SuppressWarnings("unused")
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
                handler.json(new Object() { @SuppressWarnings("unused") public boolean success = true; @SuppressWarnings("unused") public String message = "Enable scheduled"; });
            } catch (Exception e) {
                handler.status(500).json(new Object() { @SuppressWarnings("unused") public boolean success = false; @SuppressWarnings("unused") public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("plugins/{name}/disable"), handler -> {
            String name = handler.pathParam("name");
            try {
                pluginService.disablePlugin(name);
                handler.json(new Object() { @SuppressWarnings("unused") public boolean success = true; @SuppressWarnings("unused") public String message = "Disable scheduled"; });
            } catch (Exception e) {
                handler.status(500).json(new Object() { @SuppressWarnings("unused") public boolean success = false; @SuppressWarnings("unused") public String message = e.getMessage(); });
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
                handler.status(500).json(new Object() { @SuppressWarnings("unused") public boolean success = false; @SuppressWarnings("unused") public String message = e.getMessage(); });
            }
        });

        app.get(API.getFullPath("curseforge/mods/{modId}/files"), handler -> {
            try {
                String modId = handler.pathParam("modId");
                String response = curseForgeService.getModFiles(modId);
                handler.contentType("application/json").result(response);
            } catch (Exception e) {
                plugin.getLogger().warning("Error fetching mod files: " + e.getMessage());
                handler.status(500).json(new Object() { @SuppressWarnings("unused") public boolean success = false; @SuppressWarnings("unused") public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("curseforge/install"), handler -> {
            try {
                String modId = handler.queryParam("modId");
                String fileId = handler.queryParam("fileId");
                
                plugin.getLogger().info("📦 Installing mod: modId=" + modId + ", fileId=" + fileId);
                
                if (modId == null || fileId == null) {
                    handler.status(400).json(new Object() { @SuppressWarnings("unused") public boolean success = false; @SuppressWarnings("unused") public String message = "Missing modId or fileId"; });
                    return;
                }

                String fileInfoJson = curseForgeService.getFileInfo(modId, fileId);
                plugin.getLogger().info("📄 File info received: " + fileInfoJson.substring(0, Math.min(200, fileInfoJson.length())) + "...");
                
                String downloadUrl = curseForgeService.extractJsonField(fileInfoJson, "downloadUrl");
                String fileName = curseForgeService.extractJsonField(fileInfoJson, "fileName");
                
                plugin.getLogger().info("🔍 Extracted - fileName: " + fileName + ", downloadUrl: " + downloadUrl);
                
                if (downloadUrl == null || fileName == null) {
                    plugin.getLogger().warning("❌ Failed to extract file info from JSON");
                    handler.status(500).json(new Object() { @SuppressWarnings("unused") public boolean success = false; @SuppressWarnings("unused") public String message = "Failed to parse file info. downloadUrl or fileName is null"; });
                    return;
                }
                
                if (downloadUrl.isEmpty() || !downloadUrl.startsWith("http")) {
                    plugin.getLogger().warning("❌ Invalid download URL: " + downloadUrl);
                    handler.status(500).json(new Object() { @SuppressWarnings("unused") public boolean success = false; @SuppressWarnings("unused") public String message = "Invalid download URL: " + downloadUrl; });
                    return;
                }

                curseForgeService.installMod(modId, fileId, fileName, downloadUrl);
                handler.json(new Object() { 
                    @SuppressWarnings("unused")
                    public boolean success = true; 
                    @SuppressWarnings("unused")
                    public String message = "Installed to /plugins"; 
                    @SuppressWarnings("unused")
                    public String path = new File(plugin.getDataFolder().getParentFile(), fileName).getAbsolutePath(); 
                });
            } catch (Exception e) {
                plugin.getLogger().warning("Error installing plugin: " + e.getMessage());
                e.printStackTrace();
                handler.status(500).json(new Object() { @SuppressWarnings("unused") public boolean success = false; @SuppressWarnings("unused") public String message = e.getMessage(); });
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
            
            // Enable CORS in Javalin 6.x
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
            
            // Log all requests for debugging
            config.requestLogger.http((ctx, ms) -> {
                String statusEmoji = ctx.status().getCode() == 200 ? "✅" : 
                                   ctx.status().getCode() == 404 ? "❌" : "⚠️";
                plugin.getLogger().info(String.format("%s [HTTP] %s %s - %d (%s ms)",
                    statusEmoji,
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
        
        // Add cache control headers
        app.before(ctx -> {
            String path = ctx.path();
            
            // Cache static assets (CSS, JS, fonts, images) for 1 year
            if (path.startsWith("/_next/static/") || 
                path.matches(".*\\.(css|js|woff2?|ttf|eot|svg|png|jpg|jpeg|gif|ico)$")) {
                ctx.header("Cache-Control", "public, max-age=31536000, immutable");
            } 
            // Don't cache HTML files and root
            else if (path.endsWith(".html") || path.equals("/") || 
                    (!path.startsWith("/api") && !path.contains("."))) {
                ctx.header("Cache-Control", "no-cache, no-store, must-revalidate");
                ctx.header("Pragma", "no-cache");
                ctx.header("Expires", "0");
            }
        });

        plugin.getLogger().info("====================================");
        plugin.getLogger().info("Javalin WebSocket server started!");
        plugin.getLogger().info("Port: 9092");
        plugin.getLogger().info("WebSocket: ws://localhost:9092/metrics");
        plugin.getLogger().info("REST API: http://localhost:9092/api/*");
        plugin.getLogger().info("Static files: http://localhost:9092/");
        plugin.getLogger().info("Static directory: /web (CLASSPATH)");
        
        // Log available CSS files for debugging
        try {
            java.util.Enumeration<java.net.URL> resources = 
                getClass().getClassLoader().getResources("web/_next/static/chunks/");
            if (resources.hasMoreElements()) {
                plugin.getLogger().info("✅ CSS files found in classpath");
            } else {
                plugin.getLogger().warning("⚠️ No CSS files found in classpath!");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not verify CSS files: " + e.getMessage());
        }
        
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
    
    private void registerEconomyRoutes() {
        app.get(API.getFullPath("player/{uuid}/coins"), economyEndpoint::getPlayerCoins);
        app.get(API.getFullPath("players/top"), economyEndpoint::getTopPlayers);
        plugin.getLogger().info("✅ Economy API routes registered");
    }
    
    private void registerShopRoutes() {
        app.get(API.getFullPath("shop"), shopEndpoint::getShopItems);
        app.post(API.getFullPath("shop"), shopEndpoint::addShopItem);
        app.get(API.getFullPath("shop/{id}"), shopEndpoint::getShopItem);
        app.put(API.getFullPath("shop/{id}"), shopEndpoint::updateShopItem);
        app.delete(API.getFullPath("shop/{id}"), shopEndpoint::deleteShopItem);
        plugin.getLogger().info("✅ Shop config API routes registered");
    }
    
    private void registerAfkRoutes() {
        app.get(API.getFullPath("afk"), afkEndpoint::getAfkConfig);
        app.put(API.getFullPath("afk"), afkEndpoint::updateAfkConfig);
        app.get(API.getFullPath("afk/players"), afkEndpoint::getAfkPlayers);
        plugin.getLogger().info("✅ AFK config API routes registered");
    }
    
    private void registerPluginConfigRoutes() {
        // WorldColors
        app.get(API.getFullPath("config/worldcolors"), pluginConfigEndpoint::getWorldColorsConfig);
        app.put(API.getFullPath("config/worldcolors"), pluginConfigEndpoint::updateWorldColorsConfig);
        
        // AutoShutdown
        app.get(API.getFullPath("config/autoshutdown"), pluginConfigEndpoint::getAutoShutdownConfig);
        app.put(API.getFullPath("config/autoshutdown"), pluginConfigEndpoint::updateAutoShutdownConfig);
        
        // SimpleHeal
        app.get(API.getFullPath("config/simpleheal"), pluginConfigEndpoint::getSimpleHealConfig);
        app.put(API.getFullPath("config/simpleheal"), pluginConfigEndpoint::updateSimpleHealConfig);
        
        // DeathMessage
        app.get(API.getFullPath("config/deathmessage"), pluginConfigEndpoint::getDeathMessageConfig);
        app.put(API.getFullPath("config/deathmessage"), pluginConfigEndpoint::updateDeathMessageConfig);
        
        // MobCatch
        app.get(API.getFullPath("config/mobcatch"), pluginConfigEndpoint::getMobCatchConfig);
        app.put(API.getFullPath("config/mobcatch"), pluginConfigEndpoint::updateMobCatchConfig);
        
        // FriendFeed
        app.get(API.getFullPath("config/friendfeed"), pluginConfigEndpoint::getFriendFeedConfig);
        app.put(API.getFullPath("config/friendfeed"), pluginConfigEndpoint::updateFriendFeedConfig);
        
        plugin.getLogger().info("✅ Plugin config API routes registered");
    }
    
    private void registerClanRoutes() {
        app.get(API.getFullPath("clans"), clanService::getAllClans);
        app.get(API.getFullPath("clans/{name}"), clanService::getClan);
        app.get(API.getFullPath("clans/player/{uuid}"), clanService::getClanByPlayer);
        app.get(API.getFullPath("leaderboards/clans"), clanService::getTopClans);
        plugin.getLogger().info("✅ Clan API routes registered");
    }
    
    private void registerLeaderboardRoutes() {
        app.get(API.getFullPath("leaderboards/players"), leaderboardService::getTopPlayers);
        app.get(API.getFullPath("leaderboards/stats"), leaderboardService::getCombinedStats);
        plugin.getLogger().info("✅ Leaderboard API routes registered");
    }

    private void registerLoaderScriptRoutes() {
        try {
            // Use reflection to avoid compile-time dependency on LoaderScript
            Class<?> integrationClass = Class.forName("com.webx.loaderscript.integration.LoaderScriptDashboardIntegration");
            
            // Check if LoaderScript is available
            java.lang.reflect.Method isAvailableMethod = integrationClass.getMethod("isLoaderScriptAvailable");
            Boolean isAvailable = (Boolean) isAvailableMethod.invoke(null);
            
            if (isAvailable != null && isAvailable) {
                try {
                    // Register routes
                    java.lang.reflect.Method registerMethod = integrationClass.getMethod("registerWithDashboard", Javalin.class);
                    registerMethod.invoke(null, app);
                    plugin.getLogger().info("✅ LoaderScript API routes registered successfully");
                } catch (Exception registerError) {
                    plugin.getLogger().warning("⚠️ Failed to register LoaderScript routes: " + registerError.getMessage());
                    registerError.printStackTrace();
                }
            } else {
                plugin.getLogger().info("⚠️ LoaderScript plugin not found - skipping LoaderScript routes");
            }
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("ℹ️ LoaderScript plugin not installed - skipping LoaderScript routes");
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ Error checking LoaderScript availability: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopWebServer() {
        if (app != null) {
            app.stop();
            plugin.getLogger().info("Javalin server stopped");
        }
    }
}
