package com.webx.api.endpoints;

import com.google.gson.Gson;
import com.webx.dashboard.config.PluginConfigManager;
import com.webx.dashboard.config.models.*;
import io.javalin.http.Context;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PluginConfigEndpoint {
    
    private final JavaPlugin plugin;
    private final Gson gson;
    
    public PluginConfigEndpoint(JavaPlugin plugin, Gson gson) {
        this.plugin = plugin;
        this.gson = gson;
    }
    
    // ===== WorldColors =====
    public void getWorldColorsConfig(Context ctx) {
        WorldColorsConfig config = PluginConfigManager.loadConfig("worldcolors", WorldColorsConfig.class);
        ctx.json(config);
    }
    
    public void updateWorldColorsConfig(Context ctx) {
        WorldColorsConfig config = ctx.bodyAsClass(WorldColorsConfig.class);
        PluginConfigManager.saveConfig("worldcolors", config);
        ctx.json(Map.of("success", true, "config", config));
    }
    
    // ===== AutoShutdown =====
    public void getAutoShutdownConfig(Context ctx) {
        AutoShutdownConfig config = PluginConfigManager.loadConfig("autoshutdown", AutoShutdownConfig.class);
        ctx.json(config);
    }
    
    public void updateAutoShutdownConfig(Context ctx) {
        AutoShutdownConfig config = ctx.bodyAsClass(AutoShutdownConfig.class);
        PluginConfigManager.saveConfig("autoshutdown", config);
        ctx.json(Map.of("success", true, "config", config));
    }
    
    // ===== SimpleHeal =====
    public void getSimpleHealConfig(Context ctx) {
        SimpleHealConfig config = PluginConfigManager.loadConfig("simpleheal", SimpleHealConfig.class);
        ctx.json(config);
    }
    
    public void updateSimpleHealConfig(Context ctx) {
        SimpleHealConfig config = ctx.bodyAsClass(SimpleHealConfig.class);
        PluginConfigManager.saveConfig("simpleheal", config);
        ctx.json(Map.of("success", true, "config", config));
    }
    
    // ===== DeathMessage =====
    public void getDeathMessageConfig(Context ctx) {
        DeathMessageConfig config = PluginConfigManager.loadConfig("deathmessage", DeathMessageConfig.class);
        ctx.json(config);
    }
    
    public void updateDeathMessageConfig(Context ctx) {
        DeathMessageConfig config = ctx.bodyAsClass(DeathMessageConfig.class);
        PluginConfigManager.saveConfig("deathmessage", config);
        ctx.json(Map.of("success", true, "config", config));
    }
    
    // ===== MobCatch =====
    public void getMobCatchConfig(Context ctx) {
        MobCatchConfig config = PluginConfigManager.loadConfig("mobcatch", MobCatchConfig.class);
        ctx.json(config);
    }
    
    public void updateMobCatchConfig(Context ctx) {
        MobCatchConfig config = ctx.bodyAsClass(MobCatchConfig.class);
        PluginConfigManager.saveConfig("mobcatch", config);
        ctx.json(Map.of("success", true, "config", config));
    }
    
    // ===== FriendFeed =====
    public void getFriendFeedConfig(Context ctx) {
        FriendFeedConfig config = PluginConfigManager.loadConfig("friendfeed", FriendFeedConfig.class);
        ctx.json(config);
    }
    
    public void updateFriendFeedConfig(Context ctx) {
        FriendFeedConfig config = ctx.bodyAsClass(FriendFeedConfig.class);
        PluginConfigManager.saveConfig("friendfeed", config);
        ctx.json(Map.of("success", true, "config", config));
    }
}
