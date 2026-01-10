package com.webx.regionigroks;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegionigroksMapPlugin extends JavaPlugin {
    private RegionManager regionManager;
    private File regionsFile;
    private final Map<java.util.UUID, PendingRegion> pendingRegions = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("RegionigroksMap enabled");

        // Register /regionmap command
        PluginCommand cmd = getCommand("regionmap");
        if (cmd != null) {
            cmd.setExecutor(new RegionMapCommand(this));
        } else {
            getLogger().warning("Command 'regionmap' not found in plugin.yml");
        }

        // Init region manager
        this.regionManager = new RegionManager();
        this.regionsFile = new File(getDataFolder(), "regions.json");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        try {
            regionManager.load(regionsFile);
            getLogger().info("Loaded regions from " + regionsFile.getAbsolutePath());
        } catch (IOException e) {
            getLogger().warning("Failed to load regions: " + e.getMessage());
        }

        // Register /region command for creation GUI
        PluginCommand regionCmd = getCommand("region");
        if (regionCmd != null) {
            regionCmd.setExecutor(new RegionCreateCommand(this));
        } else {
            getLogger().warning("Command 'region' not found in plugin.yml");
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new CreateRegionListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
    }

    @Override
    public void onDisable() {
        try {
            regionManager.save(regionsFile);
            getLogger().info("Saved regions to " + regionsFile.getAbsolutePath());
        } catch (IOException e) {
            getLogger().warning("Failed to save regions: " + e.getMessage());
        }
        getLogger().info("RegionigroksMap disabled");
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public Map<java.util.UUID, PendingRegion> getPendingRegions() {
        return pendingRegions;
    }

    public void saveRegions() {
        try {
            regionManager.save(regionsFile);
        } catch (IOException e) {
            getLogger().warning("Failed to save regions: " + e.getMessage());
        }
    }
}
