package com.webx.loaderscript;

import com.webx.loaderscript.api.ScriptAPIController;
import com.webx.loaderscript.commands.LoaderScriptCommand;
import com.webx.loaderscript.manager.ScriptManager;
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
    private ScriptAPIController apiController;
    
    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        
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
        
        // Create API controller (register with WebX Dashboard later)
        apiController = new ScriptAPIController(scriptManager, jsEngine);
        
        // Load all scripts
        scriptManager.loadAllScripts();
        
        getLogger().info("LoaderScript enabled! Scripts folder: " + scriptsFolder.getAbsolutePath());
        getLogger().info("Loaded " + scriptManager.getLoadedScripts().size() + " scripts");
    }
    
    @Override
    public void onDisable() {
        // Unload all scripts
        if (scriptManager != null) {
            scriptManager.unloadAllScripts();
        }
        
        getLogger().info("LoaderScript disabled!");
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
     * Get API controller for registering with WebX Dashboard
     */
    public ScriptAPIController getAPIController() {
        return apiController;
    }
}
