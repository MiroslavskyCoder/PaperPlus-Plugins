package com.webx.fromdrop;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class DropConfig {
    private final Map<Material, List<DropEntry>> blockDrops = new HashMap<>();
    private final Map<Material, List<DropEntry>> mobDrops = new HashMap<>();
    private boolean announce;
    private String message;

    public void load(JavaPlugin plugin) {
        blockDrops.clear();
        mobDrops.clear();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        announce = plugin.getConfig().getBoolean("settings.announce-drops", true);
        message = plugin.getConfig().getString("settings.message", "You received bonus drop: %item% x%amount%");

        ConfigurationSection blocks = plugin.getConfig().getConfigurationSection("blocks");
        if (blocks != null) {
            for (String key : blocks.getKeys(false)) {
                Material mat = Material.matchMaterial(key);
                if (mat == null) continue;
                List<DropEntry> entries = parseEntries(blocks.getConfigurationSection(key));
                if (!entries.isEmpty()) blockDrops.put(mat, entries);
            }
        }

        ConfigurationSection mobs = plugin.getConfig().getConfigurationSection("mobs");
        if (mobs != null) {
            for (String key : mobs.getKeys(false)) {
                Material mat = Material.matchMaterial(key);
                if (mat == null) continue;
                List<DropEntry> entries = parseEntries(mobs.getConfigurationSection(key));
                if (!entries.isEmpty()) mobDrops.put(mat, entries);
            }
        }
    }

    private List<DropEntry> parseEntries(ConfigurationSection section) {
        List<DropEntry> list = new ArrayList<>();
        if (section == null) return list;
        for (String idx : section.getKeys(false)) {
            Material mat = Material.matchMaterial(section.getString(idx + ".item", ""));
            int amount = section.getInt(idx + ".amount", 1);
            double chance = section.getDouble(idx + ".chance", 0.0);
            if (mat != null && amount > 0 && chance > 0.0) {
                list.add(new DropEntry(mat, amount, chance));
            }
        }
        return list;
    }

    public Optional<List<DropEntry>> getBlockDrops(Material mat) {
        return Optional.ofNullable(blockDrops.get(mat));
    }

    public Optional<List<DropEntry>> getMobDrops(Material mat) {
        return Optional.ofNullable(mobDrops.get(mat));
    }

    public boolean isAnnounce() {
        return announce;
    }

    public String getMessage() {
        return message;
    }
}
