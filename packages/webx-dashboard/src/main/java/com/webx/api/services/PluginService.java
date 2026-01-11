package com.webx.api.services;

import java.io.File;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PluginService {
    
    private final JavaPlugin plugin;
    
    public PluginService(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public Object[] getPlugins() {
        Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
        
        Object[] pluginProfiles = new Object[plugins.length];
        for (int i = 0; i < plugins.length; i++) {
            Plugin p = plugins[i];
            pluginProfiles[i] = new Object() {
                @SuppressWarnings("unused")
                public String name = p.getName();
                @SuppressWarnings({"deprecation", "unused"})
                public String version = p.getDescription().getVersion();
                @SuppressWarnings("unused")
                public boolean isEnabled = p.isEnabled();
                @SuppressWarnings({"deprecation", "unused"})
                public String description = p.getDescription().getDescription();
                @SuppressWarnings({"deprecation", "unused"})
                public String author = p.getDescription().getAuthors().isEmpty() ? "" : p.getDescription().getAuthors().get(0);
                @SuppressWarnings({"deprecation", "unused"})
                public String website = p.getDescription().getWebsite();
            };
        }
        return pluginProfiles;
    }
    
    public void enablePlugin(String name) {
        new BukkitRunnable() {
            public void run() {
                org.bukkit.plugin.PluginManager pm = plugin.getServer().getPluginManager();
                Plugin p = pm.getPlugin(name);
                
                if (p == null) {
                    File pluginsDir = plugin.getDataFolder().getParentFile();
                    File[] jars = pluginsDir.listFiles((dir, fname) -> 
                        fname.toLowerCase().endsWith(".jar") && 
                        fname.toLowerCase().contains(name.toLowerCase())
                    );
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
    }
    
    public void disablePlugin(String name) {
        new BukkitRunnable() {
            public void run() {
                org.bukkit.plugin.PluginManager pm = plugin.getServer().getPluginManager();
                Plugin p = pm.getPlugin(name);
                if (p != null && p.isEnabled()) {
                    pm.disablePlugin(p);
                    plugin.getLogger().info("🛑 Disabled plugin: " + p.getName());
                }
            }
        }.runTask(plugin);
    }
}
