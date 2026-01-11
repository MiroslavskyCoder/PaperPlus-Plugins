package com.webx.clans.managers;

import com.webx.clans.ClansPlugin;
import org.bukkit.Chunk;

import java.util.*;

public class TerritoryManager {
    private final ClansPlugin plugin;
    private final Map<String, Set<String>> territories; // clanName -> chunks

    public TerritoryManager(ClansPlugin plugin) {
        this.plugin = plugin;
        this.territories = new HashMap<>();
    }

    public void claimTerritory(String clanName, Chunk chunk) {
        String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        territories.computeIfAbsent(clanName, k -> new HashSet<>()).add(chunkKey);
    }

    public void unclaimTerritory(String clanName, Chunk chunk) {
        String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        Set<String> chunks = territories.get(clanName);
        if (chunks != null) {
            chunks.remove(chunkKey);
        }
    }

    public String getClaimingClan(Chunk chunk) {
        String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        return territories.entrySet().stream()
                .filter(entry -> entry.getValue().contains(chunkKey))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public int getClaimedChunks(String clanName) {
        Set<String> chunks = territories.get(clanName);
        return chunks != null ? chunks.size() : 0;
    }
}
