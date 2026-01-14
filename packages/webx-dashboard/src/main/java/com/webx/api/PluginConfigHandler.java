package com.webx.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.webx.dashboard.config.PluginConfigManager;
import com.webx.dashboard.config.models.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PluginConfigHandler implements HttpHandler {
    
    private static final Gson GSON = new Gson();
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        
        if (method.equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        
        try {
            if (path.startsWith("/api/config/")) {
                String pluginName = path.substring("/api/config/".length());
                
                if (method.equals("GET")) {
                    handleGetConfig(exchange, pluginName);
                } else if (method.equals("PUT")) {
                    handleUpdateConfig(exchange, pluginName);
                } else {
                    sendResponse(exchange, 405, Map.of("error", "Method not allowed"));
                }
            } else if (path.equals("/api/config")) {
                if (method.equals("GET")) {
                    handleGetAllConfigs(exchange);
                } else {
                    sendResponse(exchange, 405, Map.of("error", "Method not allowed"));
                }
            } else {
                sendResponse(exchange, 404, Map.of("error", "Not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, Map.of("error", e.getMessage()));
        }
    }
    
    private void handleGetConfig(HttpExchange exchange, String pluginName) throws IOException {
        Object config = loadConfigByName(pluginName);
        
        if (config == null) {
            sendResponse(exchange, 404, Map.of("error", "Plugin not found"));
            return;
        }
        
        sendResponse(exchange, 200, config);
    }
    
    private void handleUpdateConfig(HttpExchange exchange, String pluginName) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> updates = GSON.fromJson(requestBody, Map.class);
        
        // Save the config
        PluginConfigManager.saveConfigFromMap(pluginName, updates);
        
        // Reload plugin config
        Object config = loadConfigByName(pluginName);
        sendResponse(exchange, 200, Map.of("success", true, "config", config));
    }
    
    private void handleGetAllConfigs(HttpExchange exchange) throws IOException {
        Map<String, Object> allConfigs = new HashMap<>();
        
        allConfigs.put("worldcolors", PluginConfigManager.loadConfig("worldcolors", WorldColorsConfig.class));
        allConfigs.put("autoshutdown", PluginConfigManager.loadConfig("autoshutdown", AutoShutdownConfig.class));
        allConfigs.put("simpleheal", PluginConfigManager.loadConfig("simpleheal", SimpleHealConfig.class));
        allConfigs.put("deathmessage", PluginConfigManager.loadConfig("deathmessage", DeathMessageConfig.class));
        allConfigs.put("mobcatch", PluginConfigManager.loadConfig("mobcatch", MobCatchConfig.class));
        allConfigs.put("friendfeed", PluginConfigManager.loadConfig("friendfeed", FriendFeedConfig.class));
        
        sendResponse(exchange, 200, allConfigs);
    }
    
    private Object loadConfigByName(String pluginName) {
        return switch (pluginName.toLowerCase()) {
            case "worldcolors" -> PluginConfigManager.loadConfig("worldcolors", WorldColorsConfig.class);
            case "autoshutdown" -> PluginConfigManager.loadConfig("autoshutdown", AutoShutdownConfig.class);
            case "simpleheal" -> PluginConfigManager.loadConfig("simpleheal", SimpleHealConfig.class);
            case "deathmessage" -> PluginConfigManager.loadConfig("deathmessage", DeathMessageConfig.class);
            case "mobcatch" -> PluginConfigManager.loadConfig("mobcatch", MobCatchConfig.class);
            case "friendfeed" -> PluginConfigManager.loadConfig("friendfeed", FriendFeedConfig.class);
            default -> null;
        };
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String json = GSON.toJson(data);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
