package com.webx.api;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
    private Set<WsContext> playersClients = new CopyOnWriteArraySet<>();
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
                plugin.getLogger().info("✅ WebSocket client connected: " + ctx.sessionId() + " | Total clients: " + clients.size());
            });
            ws.onClose(ctx -> {
                clients.remove(ctx);
                plugin.getLogger().info("❌ WebSocket client disconnected: " + ctx.sessionId() + " | Total clients: " + clients.size());
            });
            ws.onError(ctx -> {
                Throwable err = ctx.error();
                // ClosedChannelException is normal when client disconnects
                if (err instanceof java.nio.channels.ClosedChannelException) {
                    plugin.getLogger().info("WS closed (metrics): " + ctx.sessionId());
                } else {
                    plugin.getLogger().warning("⚠️ WebSocket error (metrics) for " + ctx.sessionId() + ": " + err);
                }
                clients.remove(ctx);
            });
        });

        // Players metrics WebSocket endpoint
        app.ws("/players/metrics", ws -> {
            plugin.getLogger().info("🎮 WebSocket /players/metrics endpoint initialized");
            ws.onConnect(ctx -> {
                playersClients.add(ctx);
                plugin.getLogger().info("✅ Players WebSocket connected: " + ctx.sessionId() + " | Total: " + playersClients.size());
                // Send initial player data
                sendPlayersMetrics();
            });
            ws.onClose(ctx -> {
                playersClients.remove(ctx);
                plugin.getLogger().info("❌ Players WebSocket disconnected: " + ctx.sessionId() + " | Total: " + playersClients.size());
            });
            ws.onError(ctx -> {
                Throwable err = ctx.error();
                if (err instanceof java.nio.channels.ClosedChannelException) {
                    plugin.getLogger().info("WS closed (players): " + ctx.sessionId());
                } else {
                    plugin.getLogger().warning("⚠️ Players WebSocket error: " + err);
                }
                playersClients.remove(ctx);
            });
        });

        app.get(API.getFullPath("players"), handler -> {
            plugin.getLogger().info("📊 GET /api/players request received");
            handler.json(plugin.getServer().getOnlinePlayers().stream().map(player -> {
                return new com.webx.player.PlayerProfile(player);
            }).toArray());
            plugin.getLogger().info("✅ GET /api/players response sent");
        });

        // Player Actions endpoints
        app.post(API.getFullPath("players/{uuid}/kick"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                String reason = handler.queryParam("reason");
                if (reason == null) reason = "Kicked by admin";
                
                java.util.UUID uuid = java.util.UUID.fromString(playerUuid);
                String finalReason = reason;
                
                new org.bukkit.scheduler.BukkitRunnable() {
                    public void run() {
                        org.bukkit.entity.Player player = plugin.getServer().getPlayer(uuid);
                        if (player != null && player.isOnline()) {
                            player.kickPlayer(finalReason);
                            plugin.getLogger().info("👢 Kicked player: " + player.getName() + " Reason: " + finalReason);
                        }
                    }
                }.runTask(plugin);
                
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
                if (reason == null) reason = "Banned by admin";
                
                java.util.UUID uuid = java.util.UUID.fromString(playerUuid);
                String finalReason = reason;
                
                new org.bukkit.scheduler.BukkitRunnable() {
                    public void run() {
                        org.bukkit.entity.Player player = plugin.getServer().getPlayer(uuid);
                        if (player != null) {
                            org.bukkit.BanEntry ban = plugin.getServer().getBanList(org.bukkit.BanList.Type.NAME).addBan(player.getName(), finalReason, null, "Admin");
                            if (player.isOnline()) {
                                player.kickPlayer("You have been banned: " + finalReason);
                            }
                            plugin.getLogger().info("🚫 Banned player: " + player.getName() + " Reason: " + finalReason);
                        }
                    }
                }.runTask(plugin);
                
                handler.json(new Object() { public boolean success = true; public String message = "Player ban scheduled"; });
            } catch (Exception e) {
                plugin.getLogger().warning("Error banning player: " + e.getMessage());
                handler.status(400).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("players/{uuid}/teleport"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                java.util.UUID uuid = java.util.UUID.fromString(playerUuid);
                
                new org.bukkit.scheduler.BukkitRunnable() {
                    public void run() {
                        org.bukkit.entity.Player player = plugin.getServer().getPlayer(uuid);
                        if (player != null && player.isOnline()) {
                            player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
                            plugin.getLogger().info("📍 Teleported player: " + player.getName() + " to spawn");
                        }
                    }
                }.runTask(plugin);
                
                handler.json(new Object() { public boolean success = true; public String message = "Player teleport scheduled"; });
            } catch (Exception e) {
                plugin.getLogger().warning("Error teleporting player: " + e.getMessage());
                handler.status(400).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        app.post(API.getFullPath("players/{uuid}/heal"), handler -> {
            try {
                String playerUuid = handler.pathParam("uuid");
                java.util.UUID uuid = java.util.UUID.fromString(playerUuid);
                
                new org.bukkit.scheduler.BukkitRunnable() {
                    public void run() {
                        org.bukkit.entity.Player player = plugin.getServer().getPlayer(uuid);
                        if (player != null && player.isOnline()) {
                            player.setHealth(player.getMaxHealth());
                            player.setFoodLevel(20);
                            plugin.getLogger().info("💊 Healed player: " + player.getName());
                        }
                    }
                }.runTask(plugin);
                
                handler.json(new Object() { public boolean success = true; public String message = "Player heal scheduled"; });
            } catch (Exception e) {
                plugin.getLogger().warning("Error healing player: " + e.getMessage());
                handler.status(400).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        // Server Status endpoint
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

        // Enable a plugin by name (load if needed)
        app.post(API.getFullPath("plugins/{name}/enable"), handler -> {
            String name = handler.pathParam("name");
            try {
                new org.bukkit.scheduler.BukkitRunnable() {
                    public void run() {
                        org.bukkit.plugin.PluginManager pm = plugin.getServer().getPluginManager();
                        Plugin p = pm.getPlugin(name);
                        if (p == null) {
                            // try to load from /plugins directory
                            File pluginsDir = plugin.getDataFolder().getParentFile();
                            File[] jars = pluginsDir.listFiles((dir, fname) -> fname.toLowerCase().endsWith(".jar") && fname.toLowerCase().contains(name.toLowerCase()));
                            if (jars != null) {
                                for (File jar : jars) {
                                    try {
                                        p = pm.loadPlugin(jar);
                                        break;
                                    } catch (Exception ignored) {}
                                }
                            }
                        }
                        if (p != null && !p.isEnabled()) {
                            pm.enablePlugin(p);
                            plugin.getLogger().info("✅ Enabled plugin: " + p.getName());
                        }
                    }
                }.runTask(plugin);
                handler.json(new Object() { public boolean success = true; public String message = "Enable scheduled"; });
            } catch (Exception e) {
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        // Disable a plugin by name
        app.post(API.getFullPath("plugins/{name}/disable"), handler -> {
            String name = handler.pathParam("name");
            try {
                new org.bukkit.scheduler.BukkitRunnable() {
                    public void run() {
                        org.bukkit.plugin.PluginManager pm = plugin.getServer().getPluginManager();
                        Plugin p = pm.getPlugin(name);
                        if (p != null && p.isEnabled()) {
                            pm.disablePlugin(p);
                            plugin.getLogger().info("🛑 Disabled plugin: " + p.getName());
                        }
                    }
                }.runTask(plugin);
                handler.json(new Object() { public boolean success = true; public String message = "Disable scheduled"; });
            } catch (Exception e) {
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        // ===== CURSEFORGE PLUGIN MANAGER =====
        // Proxy search to CurseForge API (requires X-Api-Key)
        app.get(API.getFullPath("curseforge/search"), handler -> {
            try {
                String apiKey = getCurseForgeApiKey(handler);
                if (apiKey == null || apiKey.isEmpty()) {
                    handler.status(400).json(new Object() { public boolean success = false; public String message = "Missing CurseForge API key (set env CURSEFORGE_API_KEY or pass ?apiKey=)"; });
                    return;
                }

                String q = handler.queryParam("q");
                if (q == null || q.isEmpty()) {
                    handler.status(400).json(new Object() { public boolean success = false; public String message = "Missing query parameter 'q'"; });
                    return;
                }

                String gameId = handler.queryParam("gameId");
                if (gameId == null || gameId.isEmpty()) gameId = "432"; // Minecraft
                String classId = handler.queryParam("classId"); // optional, allow caller to filter (e.g., Bukkit plugins)

                StringBuilder url = new StringBuilder("https://api.curseforge.com/v1/mods/search?gameId=").append(gameId)
                    .append("&searchFilter=").append(URLEncoder.encode(q, "UTF-8"));
                if (classId != null && !classId.isEmpty()) url.append("&classId=").append(classId);

                String response = httpGet(url.toString(), apiKey);
                handler.contentType("application/json").result(response);
            } catch (Exception e) {
                plugin.getLogger().warning("Error proxying CurseForge search: " + e.getMessage());
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        // List files for a specific mod
        app.get(API.getFullPath("curseforge/mods/{modId}/files"), handler -> {
            try {
                String apiKey = getCurseForgeApiKey(handler);
                if (apiKey == null || apiKey.isEmpty()) {
                    handler.status(400).json(new Object() { public boolean success = false; public String message = "Missing CurseForge API key"; });
                    return;
                }
                String modId = handler.pathParam("modId");
                String url = "https://api.curseforge.com/v1/mods/" + modId + "/files";
                String response = httpGet(url, apiKey);
                handler.contentType("application/json").result(response);
            } catch (Exception e) {
                plugin.getLogger().warning("Error fetching mod files: " + e.getMessage());
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
        });

        // Install a specific mod file into /plugins
        app.post(API.getFullPath("curseforge/install"), handler -> {
            try {
                String apiKey = getCurseForgeApiKey(handler);
                if (apiKey == null || apiKey.isEmpty()) {
                    handler.status(400).json(new Object() { public boolean success = false; public String message = "Missing CurseForge API key"; });
                    return;
                }
                String modId = handler.queryParam("modId");
                String fileId = handler.queryParam("fileId");
                if (modId == null || fileId == null) {
                    handler.status(400).json(new Object() { public boolean success = false; public String message = "Missing modId or fileId"; });
                    return;
                }

                String fileInfoJson = httpGet("https://api.curseforge.com/v1/mods/" + modId + "/files/" + fileId, apiKey);
                // Extract downloadUrl and fileName using a simple approach to avoid adding dependencies
                String downloadUrl = extractJsonField(fileInfoJson, "downloadUrl");
                String fileName = extractJsonField(fileInfoJson, "fileName");
                if (downloadUrl == null || fileName == null) {
                    handler.status(500).json(new Object() { public boolean success = false; public String message = "Failed to parse file info"; });
                    return;
                }

                File pluginsDir = plugin.getDataFolder().getParentFile(); // /plugins
                File target = new File(pluginsDir, fileName);

                downloadToFile(downloadUrl, target);
                plugin.getLogger().info("⬇️ Downloaded plugin: " + fileName + " to " + target.getAbsolutePath());

                handler.json(new Object() { public boolean success = true; public String message = "Installed to /plugins"; public String path = target.getAbsolutePath(); });
            } catch (Exception e) {
                plugin.getLogger().warning("Error installing plugin: " + e.getMessage());
                handler.status(500).json(new Object() { public boolean success = false; public String message = e.getMessage(); });
            }
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
            plugin.getLogger().info("📊 GET /api/settings request received");
            handler.json(settingsService.getSettings());
            plugin.getLogger().info("✅ GET /api/settings response sent");
        });

        // PUT /api/settings - Update settings
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
            double cpuLoad = SystemHelper.getCpuLoad();
            // Normalize CPU usage to 0-100% range (handle both 0-1 and 0-100 scales)
            double cpuUsage = cpuLoad > 100 ? Math.min(cpuLoad / Runtime.getRuntime().availableProcessors(), 100) : 
                              cpuLoad <= 1 ? cpuLoad * 100 : cpuLoad;
            
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
                // plugin.getLogger().info("📤 Sending metrics to " + clients.size() + " client(s)");
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

    // ===== Helper methods for CurseForge integration =====
    private String getCurseForgeApiKey(io.javalin.http.Context handler) {
        String fromParam = handler.queryParam("apiKey");
        if (fromParam != null && !fromParam.isEmpty()) return fromParam;
        String env = System.getenv("CURSEFORGE_API_KEY");
        return env;
    }

    private String httpGet(String urlStr, String apiKey) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("x-api-key", apiKey);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(20000);
        try (InputStream in = conn.getInputStream()) {
            return new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    private void downloadToFile(String urlStr, File target) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(60000);
        try (BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
             FileOutputStream fos = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }
        }
    }

    private String extractJsonField(String json, String field) {
        // Very simple parser to avoid adding dependencies here
        // Looks for "field":"value" or "field": "value"
        String pattern = "\"" + field + "\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx);
        if (colon == -1) return null;
        int startQuote = json.indexOf('"', colon + 1);
        if (startQuote == -1) return null;
        int endQuote = json.indexOf('"', startQuote + 1);
        if (endQuote == -1) return null;
        return json.substring(startQuote + 1, endQuote);
    }

    private void sendPlayersMetrics() {
        try {
            Object[] playersData = plugin.getServer().getOnlinePlayers().stream().map(player -> {
                return new Object() {
                    public String name = player.getName();
                    public String uuid = player.getUniqueId().toString();
                    public double health = player.getHealth();
                    public double maxHealth = player.getMaxHealth();
                    public double x = player.getLocation().getX();
                    public double y = player.getLocation().getY();
                    public double z = player.getLocation().getZ();
                    public String world = player.getWorld().getName();
                    public int level = player.getLevel();
                    public float experience = player.getExp();
                    public int foodLevel = player.getFoodLevel();
                    public long ping = player.getPing();
                    public boolean online = player.isOnline();
                    public long timestamp = System.currentTimeMillis();
                };
            }).toArray();

            String json = objectMapper.writeValueAsString(playersData);

            if (!playersClients.isEmpty()) {
                // plugin.getLogger().info("📤 Sending player metrics to " + playersClients.size() + " client(s)");
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

    private void startWebServer() {
        app = Javalin.create(config -> {
            // Serve static files from /web directory in resources
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/web";
                staticFiles.location = io.javalin.http.staticfiles.Location.CLASSPATH;
                staticFiles.precompress = false;
                staticFiles.aliasCheck = null;
            });
            
            // Enable CORS for all origins
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
            
            // Add request logging
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

        // Start metrics collection every 2 seconds (40 ticks = 2 seconds on 20 TPS server)
        new BukkitRunnable() {
            @Override
            public void run() {
                collectAndSendMetrics();
            }
        }.runTaskTimer(plugin, 0L, 40L); // 40 ticks = 2 seconds

        // Start players metrics collection every 1 second (20 ticks)
        new BukkitRunnable() {
            @Override
            public void run() {
                sendPlayersMetrics();
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 second

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
