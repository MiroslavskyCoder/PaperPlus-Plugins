package com.webx.loaderscript.manager;

import com.webx.loaderscript.LoaderScriptPlugin;
import com.webx.loaderscript.models.ScriptInfo;
import lxxv.shared.javascript.JavaScriptEngine;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages loading, unloading, and execution of JavaScript scripts
 */
public class ScriptManager {
    
    private final LoaderScriptPlugin plugin;
    private final File scriptsFolder;
    private final JavaScriptEngine jsEngine;
    private final Map<String, ScriptInfo> loadedScripts = new ConcurrentHashMap<>();
    
    public ScriptManager(LoaderScriptPlugin plugin, File scriptsFolder, JavaScriptEngine jsEngine) {
        this.plugin = plugin;
        this.scriptsFolder = scriptsFolder;
        this.jsEngine = jsEngine;
    }
    
    /**
     * Load all scripts from scripts folder
     */
    public void loadAllScripts() {
        if (!scriptsFolder.exists()) {
            plugin.getLogger().warning("Scripts folder does not exist: " + scriptsFolder.getAbsolutePath());
            return;
        }
        
        File[] files = scriptsFolder.listFiles((dir, name) -> 
            name.endsWith(".js") || name.endsWith(".ts") || name.endsWith(".jsx") || name.endsWith(".tsx")
        );
        
        if (files == null || files.length == 0) {
            plugin.getLogger().info("No scripts found in scripts folder");
            return;
        }
        
        int loaded = 0;
        for (File file : files) {
            if (loadScript(file.getName())) {
                loaded++;
            }
        }
        
        plugin.getLogger().info("Loaded " + loaded + "/" + files.length + " scripts");
    }
    
    /**
     * Load a specific script by name
     */
    public boolean loadScript(String scriptName) {
        File scriptFile = new File(scriptsFolder, scriptName);
        
        if (!scriptFile.exists()) {
            plugin.getLogger().warning("Script not found: " + scriptName);
            return false;
        }
        
        try {
            // Read script content
            String content = Files.readString(scriptFile.toPath(), StandardCharsets.UTF_8);
            
            // Check if TypeScript/JSX - transpile first
            final String executableCode;
            if (isTypeScriptFile(scriptName)) {
                plugin.getLogger().info("Transpiling TypeScript: " + scriptName);
                executableCode = jsEngine.transpile(content, scriptName);
            } else {
                executableCode = content;
            }
            
            // Execute script in sync context (main thread)
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    Object result = jsEngine.execute(executableCode, new HashMap<>());
                    
                    // Create script info
                    ScriptInfo info = new ScriptInfo(
                        scriptName,
                        scriptFile.getAbsolutePath(),
                        scriptFile.length(),
                        scriptFile.lastModified(),
                        System.currentTimeMillis(),
                        true,
                        null
                    );
                    
                    loadedScripts.put(scriptName, info);
                    plugin.getLogger().info("§a✓ Loaded script: " + scriptName);
                    
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to execute script " + scriptName + ": " + e.getMessage());
                    
                    // Store error info
                    ScriptInfo info = new ScriptInfo(
                        scriptName,
                        scriptFile.getAbsolutePath(),
                        scriptFile.length(),
                        scriptFile.lastModified(),
                        System.currentTimeMillis(),
                        false,
                        e.getMessage()
                    );
                    loadedScripts.put(scriptName, info);
                }
            });
            
            return true;
            
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to read script " + scriptName + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load script " + scriptName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reload a specific script
     */
    public boolean reloadScript(String scriptName) {
        unloadScript(scriptName);
        return loadScript(scriptName);
    }
    
    /**
     * Unload a specific script
     */
    public boolean unloadScript(String scriptName) {
        ScriptInfo info = loadedScripts.remove(scriptName);
        if (info != null) {
            plugin.getLogger().info("§c✗ Unloaded script: " + scriptName);
            return true;
        }
        return false;
    }
    
    /**
     * Unload all scripts
     */
    public void unloadAllScripts() {
        int count = loadedScripts.size();
        loadedScripts.clear();
        plugin.getLogger().info("Unloaded " + count + " scripts");
    }
    
    /**
     * Reload all scripts
     */
    public void reloadAllScripts() {
        unloadAllScripts();
        loadAllScripts();
    }
    
    /**
     * Get loaded scripts
     */
    public Map<String, ScriptInfo> getLoadedScripts() {
        return new HashMap<>(loadedScripts);
    }
    
    /**
     * Get script info
     */
    public ScriptInfo getScriptInfo(String scriptName) {
        return loadedScripts.get(scriptName);
    }
    
    /**
     * Check if script is loaded
     */
    public boolean isScriptLoaded(String scriptName) {
        return loadedScripts.containsKey(scriptName);
    }
    
    /**
     * List all scripts in folder (loaded and unloaded)
     */
    public List<String> listAllScripts() {
        File[] files = scriptsFolder.listFiles((dir, name) -> 
            name.endsWith(".js") || name.endsWith(".ts") || name.endsWith(".jsx") || name.endsWith(".tsx")
        );
        
        if (files == null) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(files)
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Create new script from template
     */
    public boolean createScript(String scriptName, String template) {
        // Ensure .js extension
        if (!scriptName.endsWith(".js") && !scriptName.endsWith(".ts")) {
            scriptName += ".js";
        }
        
        File scriptFile = new File(scriptsFolder, scriptName);
        
        if (scriptFile.exists()) {
            plugin.getLogger().warning("Script already exists: " + scriptName);
            return false;
        }
        
        try {
            String content = template != null ? template : getDefaultTemplate(scriptName);
            Files.writeString(scriptFile.toPath(), content, StandardCharsets.UTF_8);
            plugin.getLogger().info("Created new script: " + scriptName);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create script: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete script file
     */
    public boolean deleteScript(String scriptName) {
        // Unload first if loaded
        unloadScript(scriptName);
        
        File scriptFile = new File(scriptsFolder, scriptName);
        if (scriptFile.exists() && scriptFile.delete()) {
            plugin.getLogger().info("Deleted script: " + scriptName);
            return true;
        }
        return false;
    }
    
    /**
     * Read script content
     */
    public String readScript(String scriptName) {
        File scriptFile = new File(scriptsFolder, scriptName);
        if (!scriptFile.exists()) {
            return null;
        }
        
        try {
            return Files.readString(scriptFile.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to read script: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Write script content
     */
    public boolean writeScript(String scriptName, String content) {
        File scriptFile = new File(scriptsFolder, scriptName);
        
        try {
            Files.writeString(scriptFile.toPath(), content, StandardCharsets.UTF_8);
            plugin.getLogger().info("Updated script: " + scriptName);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write script: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if file is TypeScript
     */
    private boolean isTypeScriptFile(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".ts") || lower.endsWith(".tsx");
    }
    
    /**
     * Get default script template
     */
    private String getDefaultTemplate(String scriptName) {
        String name = scriptName.replace(".js", "").replace(".ts", "");
        return String.format("""
            /**
             * %s - Custom Script
             * Created: %s
             */
            
            // Configuration
            const CONFIG = {
                enabled: true,
                // Add your config here
            };
            
            // Initialize
            function initialize() {
                console.log('§a[%s] Script loaded!');
                
                // Your initialization code here
            }
            
            // Event handlers
            LXXVServer.on('playerJoin', (player) => {
                const playerName = player.getName();
                console.log(`§7[%s] Player joined: ${playerName}`);
            });
            
            // Commands
            LXXVServer.registerCommand('%s', (player, args) => {
                LXXVServer.sendMessage(player.getName(), '§aCommand executed!');
            });
            
            // Start
            initialize();
            """, name, new Date(), name, name, name.toLowerCase());
    }
    
    /**
     * Get scripts folder
     */
    public File getScriptsFolder() {
        return scriptsFolder;
    }
}
