package com.webx.clans.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webx.clans.ClansPlugin;
import com.webx.clans.models.Clan;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class ClanManager {
    private final ClansPlugin plugin;
    private final Map<String, Clan> clans;
    private final Gson gson;
    private final File dataFile;

    public ClanManager(ClansPlugin plugin) {
        this.plugin = plugin;
        this.clans = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataFile = new File(plugin.getDataFolder(), "clans.json");
    }

    public void loadClans() {
        clans.clear();
        
        if (!dataFile.exists()) {
            plugin.getLogger().info("No clans data file found, starting fresh");
            return;
        }

        try (Reader reader = new FileReader(dataFile)) {
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> dataList = gson.fromJson(reader, listType);
            
            if (dataList != null) {
                for (Map<String, Object> data : dataList) {
                    try {
                        Clan clan = Clan.fromMap(data);
                        clans.put(clan.getName().toLowerCase(), clan);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to load clan: " + e.getMessage());
                    }
                }
            }
            
            plugin.getLogger().info("Loaded " + clans.size() + " clans");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load clans: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveClans() {
        try {
            if (!dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }

            List<Map<String, Object>> dataList = new ArrayList<>();
            for (Clan clan : clans.values()) {
                dataList.add(clan.toMap());
            }

            try (Writer writer = new FileWriter(dataFile)) {
                gson.toJson(dataList, writer);
            }
            
            plugin.getLogger().info("Saved " + clans.size() + " clans");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save clans: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Clan createClan(String name, UUID leader) {
        if (clans.containsKey(name.toLowerCase())) {
            return null;
        }

        Clan clan = new Clan(name, leader);
        clans.put(name.toLowerCase(), clan);
        return clan;
    }

    public Clan getClan(String name) {
        return clans.get(name.toLowerCase());
    }

    public Clan getClanByMember(UUID uuid) {
        return clans.values().stream()
                .filter(clan -> clan.getMembers().contains(uuid))
                .findFirst()
                .orElse(null);
    }

    public void deleteClan(String name) {
        clans.remove(name.toLowerCase());
    }

    public Collection<Clan> getAllClans() {
        return clans.values();
    }

    public int getClanCount() {
        return clans.size();
    }
}
