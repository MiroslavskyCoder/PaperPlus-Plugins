package com.webx.hometp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {
    private final HomeTpPlugin plugin;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();
    private final File dataFile;
    private FileConfiguration data;

    public HomeManager(HomeTpPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "homes.yml");
        loadHomes();
    }

    public void loadHomes() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create homes.yml: " + e.getMessage());
                return;
            }
        }

        data = YamlConfiguration.loadConfiguration(dataFile);
        homes.clear();

        for (String uuidStr : data.getKeys(false)) {
            UUID playerId = UUID.fromString(uuidStr);
            ConfigurationSection playerSection = data.getConfigurationSection(uuidStr);
            Map<String, Location> playerHomes = new HashMap<>();

            for (String homeName : playerSection.getKeys(false)) {
                ConfigurationSection homeSection = playerSection.getConfigurationSection(homeName);
                String worldName = homeSection.getString("world");
                double x = homeSection.getDouble("x");
                double y = homeSection.getDouble("y");
                double z = homeSection.getDouble("z");
                float yaw = (float) homeSection.getDouble("yaw");
                float pitch = (float) homeSection.getDouble("pitch");

                Location loc = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
                playerHomes.put(homeName.toLowerCase(), loc);
            }

            homes.put(playerId, playerHomes);
        }
    }

    public void saveHomes() {
        data = new YamlConfiguration();

        for (Map.Entry<UUID, Map<String, Location>> entry : homes.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<String, Location> homeEntry : entry.getValue().entrySet()) {
                String path = uuidStr + "." + homeEntry.getKey();
                Location loc = homeEntry.getValue();
                data.set(path + ".world", loc.getWorld().getName());
                data.set(path + ".x", loc.getX());
                data.set(path + ".y", loc.getY());
                data.set(path + ".z", loc.getZ());
                data.set(path + ".yaw", loc.getYaw());
                data.set(path + ".pitch", loc.getPitch());
            }
        }

        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save homes.yml: " + e.getMessage());
        }
    }

    public boolean setHome(Player player, String name) {
        name = name.toLowerCase();
        UUID playerId = player.getUniqueId();
        
        Map<String, Location> playerHomes = homes.computeIfAbsent(playerId, k -> new HashMap<>());
        
        int maxHomes = plugin.getConfig().getInt("max-homes", 5);
        if (maxHomes > 0 && !playerHomes.containsKey(name) && playerHomes.size() >= maxHomes) {
            return false;
        }
        
        playerHomes.put(name, player.getLocation());
        saveHomes();
        return true;
    }

    public boolean deleteHome(Player player, String name) {
        name = name.toLowerCase();
        Map<String, Location> playerHomes = homes.get(player.getUniqueId());
        
        if (playerHomes == null || !playerHomes.containsKey(name)) {
            return false;
        }
        
        playerHomes.remove(name);
        if (playerHomes.isEmpty()) {
            homes.remove(player.getUniqueId());
        }
        saveHomes();
        return true;
    }

    public Location getHome(Player player, String name) {
        name = name.toLowerCase();
        Map<String, Location> playerHomes = homes.get(player.getUniqueId());
        return playerHomes != null ? playerHomes.get(name) : null;
    }

    public Set<String> getHomes(Player player) {
        Map<String, Location> playerHomes = homes.get(player.getUniqueId());
        return playerHomes != null ? playerHomes.keySet() : Collections.emptySet();
    }
}
