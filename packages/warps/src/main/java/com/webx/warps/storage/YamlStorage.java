package com.webx.warps.storage;

import com.webx.warps.WarpsPlugin;
import com.webx.warps.models.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YamlStorage implements StorageProvider {
    private final WarpsPlugin plugin;
    private File warpsFile;
    private FileConfiguration warpsConfig;

    public YamlStorage(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        if (!warpsFile.exists()) {
            try {
                warpsFile.getParentFile().mkdirs();
                warpsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create warps.yml: " + e.getMessage());
            }
        }
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
    }

    @Override
    public void close() {
        // Nothing to close for YAML
    }

    @Override
    public List<Warp> loadWarps() {
        List<Warp> warps = new ArrayList<>();
        
        if (warpsConfig == null) return warps;

        for (String key : warpsConfig.getKeys(false)) {
            ConfigurationSection section = warpsConfig.getConfigurationSection(key);
            if (section == null) continue;

            try {
                String worldName = section.getString("world");
                double x = section.getDouble("x");
                double y = section.getDouble("y");
                double z = section.getDouble("z");
                float yaw = (float) section.getDouble("yaw");
                float pitch = (float) section.getDouble("pitch");
                UUID creator = UUID.fromString(section.getString("creator"));

                Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
                Warp warp = new Warp(key, location, creator);

                if (section.contains("permission")) {
                    warp.setPermission(section.getString("permission"));
                }
                if (section.contains("cost")) {
                    warp.setCost(section.getDouble("cost"));
                }
                if (section.contains("icon")) {
                    warp.setIcon(section.getString("icon"));
                }
                if (section.contains("description")) {
                    warp.setDescription(section.getString("description"));
                }
                if (section.contains("enabled")) {
                    warp.setEnabled(section.getBoolean("enabled"));
                }

                warps.add(warp);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load warp: " + key);
            }
        }

        return warps;
    }

    @Override
    public void saveWarps(List<Warp> warps) {
        warpsConfig = new YamlConfiguration();

        for (Warp warp : warps) {
            saveWarpToConfig(warp);
        }

        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save warps.yml: " + e.getMessage());
        }
    }

    @Override
    public void saveWarp(Warp warp) {
        saveWarpToConfig(warp);

        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save warp: " + e.getMessage());
        }
    }

    @Override
    public void deleteWarp(String name) {
        warpsConfig.set(name, null);

        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not delete warp: " + e.getMessage());
        }
    }

    private void saveWarpToConfig(Warp warp) {
        String path = warp.getName();
        Location loc = warp.getLocation();

        warpsConfig.set(path + ".world", loc.getWorld().getName());
        warpsConfig.set(path + ".x", loc.getX());
        warpsConfig.set(path + ".y", loc.getY());
        warpsConfig.set(path + ".z", loc.getZ());
        warpsConfig.set(path + ".yaw", loc.getYaw());
        warpsConfig.set(path + ".pitch", loc.getPitch());
        warpsConfig.set(path + ".creator", warp.getCreator().toString());

        if (warp.getPermission() != null) {
            warpsConfig.set(path + ".permission", warp.getPermission());
        }
        if (warp.getCost() > 0) {
            warpsConfig.set(path + ".cost", warp.getCost());
        }
        if (warp.getIcon() != null) {
            warpsConfig.set(path + ".icon", warp.getIcon());
        }
        if (warp.getDescription() != null) {
            warpsConfig.set(path + ".description", warp.getDescription());
        }
        warpsConfig.set(path + ".enabled", warp.isEnabled());
    }
}
