package com.webx.loaderscript.integration;

import com.webx.loaderscript.LoaderScriptPlugin;
import io.javalin.Javalin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Integration helper for WebX Dashboard
 * Registers LoaderScript API routes with WebX Dashboard Javalin server
 */
public class LoaderScriptDashboardIntegration {
    
    /**
     * Register LoaderScript API routes with WebX Dashboard
     * Call this from WebX Dashboard plugin when initializing
     * 
     * Example:
     * LoaderScriptDashboardIntegration.registerWithDashboard(javalinApp);
     */
    public static void registerWithDashboard(Javalin app) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("LoaderScript");
        
        if (plugin == null || !(plugin instanceof LoaderScriptPlugin)) {
            throw new IllegalStateException("LoaderScript plugin not found or not loaded");
        }
        
        LoaderScriptPlugin loaderScript = (LoaderScriptPlugin) plugin;
        loaderScript.getAPIController().registerRoutes(app);
    }
    
    /**
     * Check if LoaderScript is available
     */
    public static boolean isLoaderScriptAvailable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("LoaderScript");
        return plugin instanceof LoaderScriptPlugin;
    }
    
    /**
     * Get LoaderScript plugin instance
     */
    public static LoaderScriptPlugin getLoaderScript() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("LoaderScript");
        if (plugin instanceof LoaderScriptPlugin) {
            return (LoaderScriptPlugin) plugin;
        }
        return null;
    }
}
