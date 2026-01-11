package com.webx.warps.storage;

import com.webx.warps.WarpsPlugin;
import com.webx.warps.models.Warp;

import java.util.ArrayList;
import java.util.List;

public class MySQLStorage implements StorageProvider {
    private final WarpsPlugin plugin;

    public MySQLStorage(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        // TODO: Implement MySQL connection
        plugin.getLogger().warning("MySQL storage not yet implemented, falling back to YAML");
    }

    @Override
    public void close() {
        // TODO: Close MySQL connection
    }

    @Override
    public List<Warp> loadWarps() {
        // TODO: Load from MySQL
        return new ArrayList<>();
    }

    @Override
    public void saveWarps(List<Warp> warps) {
        // TODO: Save to MySQL
    }

    @Override
    public void saveWarp(Warp warp) {
        // TODO: Save single warp to MySQL
    }

    @Override
    public void deleteWarp(String name) {
        // TODO: Delete from MySQL
    }
}
