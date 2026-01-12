package com.webx.loaderscript;

import com.webx.loaderscript.api.ScriptAPIController;
import com.webx.loaderscript.commands.LoaderScriptCommand;
import com.webx.loaderscript.manager.ScriptManager;
import io.javalin.Javalin;
import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.server.LXXVServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * LoaderScript - JavaScript Script Loader Plugin
 * Loads and manages JavaScript files from scripts folder with hot-reload support
 */
public class LoaderScriptPlugin extends JavaPlugin {
    
    private ScriptManager scriptManager;
    private JavaScriptEngine jsEngine;
    private Javalin apiServer;
    private int apiPort = 7072;
    
    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        apiPort = getConfig().getInt("api-port", 7072);
        
        // Initialize JavaScript engine
        jsEngine = JavaScriptEngine.getInstance();
        
        // Initialize LXXVServer bridge
        LXXVServer.initialize(Bukkit.getServer(), jsEngine);
        
        // Create scripts folder
        File scriptsFolder = new File(getDataFolder().getParentFile().getParentFile(), "scripts");
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdirs();
            getLogger().info("Created scripts folder at: " + scriptsFolder.getAbsolutePath());
        }
        
        // Initialize script manager
        scriptManager = new ScriptManager(this, scriptsFolder, jsEngine);
        
        // Register commands
        getCommand("loaderscript").setExecutor(new LoaderScriptCommand(this, scriptManager));
        
        // Start REST API server
        startAPIServer();
        
        // Load all scripts
        scriptManager.loadAllScripts();
        
        getLogger().info("LoaderScript enabled! Scripts folder: " + scriptsFolder.getAbsolutePath());
        getLogger().info("API Server running on http://localhost:" + apiPort);
        getLogger().info("Loaded " + scriptManager.getLoadedScripts().size() + " scripts");
    }
    
    @Override
    public void onDisable() {
        // Unload all scripts
        if (scriptManager != null) {
            scriptManager.unloadAllScripts();
        }
        
        // Stop API server
        if (apiServer != null) {
            apiServer.stop();
            getLogger().info("API Server stopped");
        }
        
        getLogger().info("LoaderScript disabled!");
    }
    
    /**
     * Start REST API server for web dashboard
     */
    private void startAPIServer() {
        try {
            apiServer = Javalin.create(config -> {
                config.showJavalinBanner = false;
                config.http.defaultContentType = "application/json";
            }).start(apiPort);
            
            // Register API controller
            ScriptAPIController apiController = new ScriptAPIController(scriptManager, jsEngine);
            apiController.register(apiServer);
            
            getLogger().info("API Server started on port " + apiPort);
        } catch (Exception e) {
            getLogger().severe("Failed to start API server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get script manager instance
     */
    public ScriptManager getScriptManager() {
        return scriptManager;
    }
    
    /**
     * Get JavaScript engine instance
     */
    public JavaScriptEngine getJavaScriptEngine() {
        return jsEngine;
    }
    
    /**
     * Reload plugin configuration
     */
    public void reloadPluginConfig() {
        reloadConfig();
        
        // Restart API server if port changed
        int newPort = getConfig().getInt("api-port", 7072);
        if (newPort != apiPort) {
            if (apiServer != null) {
                apiServer.stop();
            }
            apiPort = newPort;
            startAPIServer();
        }
    }
}
