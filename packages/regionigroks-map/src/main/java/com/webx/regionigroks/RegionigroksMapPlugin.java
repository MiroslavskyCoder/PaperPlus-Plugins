package com.webx.regionigroks;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

        // Register /safezone command for teleportation
        PluginCommand safeZoneCmd = getCommand("safezone");
        if (safeZoneCmd != null) {
            safeZoneCmd.setExecutor(new SafeZoneTeleportCommand(this));
        } else {
            getLogger().warning("Command 'safezone' not found in plugin.yml");
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new CreateRegionListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);

        // Initialize safe zone
        initializeSafeZone();
        
        // Start monster removal task for SafeZone
        startMonsterRemovalTask();
    }

    private void startMonsterRemovalTask() {
        // Check every 2 seconds (40 ticks) for monsters in SafeZone
        getServer().getScheduler().runTaskTimer(this, () -> {
            Optional<Region> safeZone = regionManager.getRegionByName("SafeZone");
            if (!safeZone.isPresent()) return;
            
            World world = Bukkit.getWorld("world");
            if (world == null) return;
            
            // Check all monsters in the world
            world.getEntities().stream()
                .filter(entity -> entity instanceof org.bukkit.entity.Monster)
                .filter(entity -> safeZone.get().contains(entity.getLocation()))
                .forEach(entity -> {
                    entity.remove(); // Remove monster from SafeZone
                });
        }, 40L, 40L); // Start after 2 seconds, repeat every 2 seconds
    }

    private void initializeSafeZone() {
        // Check if safe zone already exists
        if (regionManager.getRegionByName("SafeZone").isPresent()) {
            getLogger().info("Safe zone already exists");
            return;
        }

        // Get main world
        World world = Bukkit.getWorld("world");
        if (world == null) {
            getLogger().warning("World 'world' not found, cannot create safe zone");
            return;
        }

        // Create safe zone from coordinates: -963 71 -407 to -1076 60 -471
        // Calculate center and radius for circular region
        int x1 = -963, z1 = -407;
        int x2 = -1076, z2 = -471;
        
        int centerX = (x1 + x2) / 2;
        int centerZ = (z1 + z2) / 2;
        int radius = Math.max(Math.abs(x1 - centerX), Math.abs(z1 - centerZ)) + 10;
        
        Location center = new Location(world, centerX, 71, centerZ);
        
        // Create the region with server admin UUID (using a fixed admin UUID)
        UUID adminUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        Region safeZone = regionManager.createRegion("SafeZone", Color.GREEN, adminUUID, center, radius);
        
        getLogger().info("✓ Safe zone created at X:" + centerX + " Y:71 Z:" + centerZ + " (radius: " + radius + ")");
        
        try {
            regionManager.save(regionsFile);
            getLogger().info("✓ Safe zone saved to regions.json");
        } catch (IOException e) {
            getLogger().warning("Failed to save safe zone: " + e.getMessage());
        }
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
