package com.webx.warps.managers;

import com.webx.warps.WarpsPlugin;
import com.webx.warps.models.Warp;
import com.webx.warps.storage.StorageProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class WarpManager {
    private final WarpsPlugin plugin;
    private final StorageProvider storage;
    private final Map<String, Warp> warps;

    public WarpManager(WarpsPlugin plugin, StorageProvider storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.warps = new HashMap<>();
    }

    public void loadWarps() {
        warps.clear();
        List<Warp> loaded = storage.loadWarps();
        for (Warp warp : loaded) {
            warps.put(warp.getName().toLowerCase(), warp);
        }
        plugin.getLogger().info("Loaded " + warps.size() + " warps");
    }

    public void saveWarps() {
        storage.saveWarps(new ArrayList<>(warps.values()));
        plugin.getLogger().info("Saved " + warps.size() + " warps");
    }

    public boolean exists(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public boolean createWarp(String name, Location location, UUID creator) {
        if (warps.containsKey(name.toLowerCase())) {
            return false;
        }

        Warp warp = new Warp(name, location, creator);
        warps.put(name.toLowerCase(), warp);
        storage.saveWarp(warp);
        return true;
    }

    public boolean deleteWarp(String name) {
        Warp warp = warps.remove(name.toLowerCase());
        if (warp == null) {
            return false;
        }

        storage.deleteWarp(name);
        return true;
    }

    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public Collection<Warp> getAllWarps() {
        return Collections.unmodifiableCollection(warps.values());
    }

    public List<Warp> getWarpsForPlayer(Player player) {
        return warps.values().stream()
                .filter(warp -> warp.canUse(player))
                .collect(Collectors.toList());
    }

    public List<String> getWarpNames() {
        return new ArrayList<>(warps.keySet());
    }

    public int getWarpCount() {
        return warps.size();
    }

    public void updateWarp(Warp warp) {
        warps.put(warp.getName().toLowerCase(), warp);
        storage.saveWarp(warp);
    }
}
