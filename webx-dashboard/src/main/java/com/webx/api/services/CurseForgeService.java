package com.webx.api.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.bukkit.plugin.java.JavaPlugin;

import com.webx.services.SettingsService;

public class CurseForgeService {
    
    private final JavaPlugin plugin;
    private final SettingsService settingsService;
    
    public CurseForgeService(JavaPlugin plugin, SettingsService settingsService) {
        this.plugin = plugin;
        this.settingsService = settingsService;
    }
    
    public String getApiKey() {
        return settingsService.getSettings().curseforgeApiKey;
    }
    
    public String searchMods(String query, String gameId, String classId) throws Exception {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("Missing CurseForge API key in config.yml");
        }
        
        if (query == null || query.isEmpty()) {
            throw new RuntimeException("Missing query parameter");
        }
        
        if (gameId == null || gameId.isEmpty()) gameId = "432";
        // Default to Bukkit plugins (classId=5) if not specified
        if (classId == null || classId.isEmpty()) classId = "5";
        
        StringBuilder url = new StringBuilder("https://api.curseforge.com/v1/mods/search?gameId=")
            .append(gameId)
            .append("&searchFilter=")
            .append(URLEncoder.encode(query, "UTF-8"))
            .append("&classId=")
            .append(classId);
        
        plugin.getLogger().info("🔍 Searching CurseForge: " + query + " (gameId=" + gameId + ", classId=" + classId + ")");
        
        return httpGet(url.toString(), apiKey);
    }
    
    public String getModFiles(String modId) throws Exception {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("Missing CurseForge API key in config.yml");
        }
        
        String url = "https://api.curseforge.com/v1/mods/" + modId + "/files";
        return httpGet(url, apiKey);
    }
    
    public void installMod(String modId, String fileId, String fileName, String downloadUrl) throws Exception {
        File pluginsDir = plugin.getDataFolder().getParentFile();
        File target = new File(pluginsDir, fileName);
        
        downloadToFile(downloadUrl, target);
        plugin.getLogger().info("⬇️ Downloaded plugin: " + fileName + " to " + target.getAbsolutePath());
    }
    
    public String getFileInfo(String modId, String fileId) throws Exception {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("Missing CurseForge API key");
        }
        
        String url = "https://api.curseforge.com/v1/mods/" + modId + "/files/" + fileId;
        return httpGet(url, apiKey);
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
    
    public String extractJsonField(String json, String field) {
        // Find the field in JSON
        String pattern = "\"" + field + "\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) return null;
        
        int colon = json.indexOf(':', idx);
        if (colon == -1) return null;
        
        // Skip whitespace after colon
        int valueStart = colon + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }
        
        if (valueStart >= json.length()) return null;
        
        // Check if value is null
        if (json.startsWith("null", valueStart)) {
            return null;
        }
        
        // Check if value is a string (starts with quote)
        if (json.charAt(valueStart) == '"') {
            int startQuote = valueStart;
            int endQuote = json.indexOf('"', startQuote + 1);
            
            // Handle escaped quotes
            while (endQuote > 0 && json.charAt(endQuote - 1) == '\\') {
                endQuote = json.indexOf('"', endQuote + 1);
            }
            
            if (endQuote == -1) return null;
            return json.substring(startQuote + 1, endQuote);
        }
        
        // For non-string values (numbers, booleans, etc.), we can't easily extract
        // Return null to indicate this field type is not supported by simple parser
        return null;
    }
}
