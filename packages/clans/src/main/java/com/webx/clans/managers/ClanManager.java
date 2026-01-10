package com.webx.clans.managers;

import com.webx.clans.ClansPlugin;
import com.webx.clans.models.Clan;

import java.util.*;

public class ClanManager {
    private final ClansPlugin plugin;
    private final Map<String, Clan> clans;

    public ClanManager(ClansPlugin plugin) {
        this.plugin = plugin;
        this.clans = new HashMap<>();
    }

    public void loadClans() {
        clans.clear();
        // TODO: Load from storage
        plugin.getLogger().info("Loaded " + clans.size() + " clans");
    }

    public void saveClans() {
        // TODO: Save to storage
        plugin.getLogger().info("Saved " + clans.size() + " clans");
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
