package com.webx.ranks.managers;

import com.webx.ranks.models.PlayerRank;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Manages player rank assignments and tracks rank history
 */
public class PlayerRankManager {
    private final File dataFolder;
    private final File playerRanksFile;
    private Map<UUID, PlayerRank> playerRanks;
    private final Gson gson;

    public PlayerRankManager(File dataFolder) {
        this.dataFolder = new File(dataFolder, "player-ranks");
        this.playerRanksFile = new File(this.dataFolder, "player-ranks.json");
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(UUID.class, new com.google.gson.JsonSerializer<UUID>() {
                    @Override
                    public com.google.gson.JsonElement serialize(UUID src, java.lang.reflect.Type typeOfSrc,
                            com.google.gson.JsonSerializationContext context) {
                        return new com.google.gson.JsonPrimitive(src.toString());
                    }
                })
                .registerTypeAdapter(UUID.class, new com.google.gson.JsonDeserializer<UUID>() {
                    @Override
                    public UUID deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfSrc,
                            com.google.gson.JsonDeserializationContext context) {
                        return UUID.fromString(json.getAsString());
                    }
                })
                .create();
        this.playerRanks = new HashMap<>();
        
        loadData();
    }

    // ==================== Loading & Saving ====================

    public void loadData() {
        if (!playerRanksFile.exists()) {
            playerRanks = new HashMap<>();
            return;
        }

        try (Reader reader = new FileReader(playerRanksFile)) {
            Type type = new TypeToken<Map<UUID, PlayerRank>>() {}.getType();
            playerRanks = gson.fromJson(reader, type);
            if (playerRanks == null) playerRanks = new HashMap<>();
        } catch (Exception e) {
            System.err.println("Failed to load player ranks: " + e.getMessage());
            playerRanks = new HashMap<>();
        }
    }

    public void saveData() {
        try {
            dataFolder.mkdirs();
            try (Writer writer = new FileWriter(playerRanksFile)) {
                gson.toJson(playerRanks, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to save player ranks: " + e.getMessage());
        }
    }

    // ==================== Player Rank Management ====================

    public PlayerRank getOrCreatePlayerRank(UUID playerId, String playerName) {
        return playerRanks.computeIfAbsent(playerId, k -> new PlayerRank(playerId, playerName));
    }

    public PlayerRank getPlayerRank(UUID playerId) {
        return playerRanks.get(playerId);
    }

    public void setPlayerPrimaryRank(UUID playerId, String rankId, String assignedBy, String reason) {
        PlayerRank playerRank = getOrCreatePlayerRank(playerId, "");
        playerRank.setPrimaryRank(rankId);
        playerRank.setAssignedBy(assignedBy);
        playerRank.setReason(reason);
        playerRank.setActive(true);
        playerRank.setExpiresAt(0);
        saveData();
    }

    public void setPlayerRankWithExpiry(UUID playerId, String rankId, long durationMillis, 
                                        String assignedBy, String reason) {
        PlayerRank playerRank = getOrCreatePlayerRank(playerId, "");
        playerRank.setPrimaryRank(rankId);
        playerRank.setAssignedBy(assignedBy);
        playerRank.setReason(reason);
        playerRank.setExpiresAt(System.currentTimeMillis() + durationMillis);
        playerRank.setActive(true);
        saveData();
    }

    public void addAdditionalRank(UUID playerId, String rankId) {
        PlayerRank playerRank = getOrCreatePlayerRank(playerId, "");
        playerRank.addAdditionalRank(rankId);
        saveData();
    }

    public void removeAdditionalRank(UUID playerId, String rankId) {
        PlayerRank playerRank = getOrCreatePlayerRank(playerId, "");
        playerRank.removeAdditionalRank(rankId);
        saveData();
    }

    public void removePlayerRank(UUID playerId) {
        PlayerRank playerRank = playerRanks.get(playerId);
        if (playerRank != null) {
            playerRank.setActive(false);
            saveData();
        }
    }

    public String getPlayerPrimaryRank(UUID playerId) {
        PlayerRank playerRank = playerRanks.get(playerId);
        if (playerRank != null && playerRank.isActive()) {
            return playerRank.getPrimaryRank();
        }
        return "member"; // Default rank
    }

    public boolean hasRank(UUID playerId, String rankId) {
        PlayerRank playerRank = playerRanks.get(playerId);
        if (playerRank == null || !playerRank.isActive()) {
            return "member".equals(rankId);
        }
        return playerRank.hasRank(rankId);
    }

    public Map<UUID, PlayerRank> getAllPlayerRanks() {
        return new HashMap<>(playerRanks);
    }

    public List<PlayerRank> getPlayersWithRank(String rankId) {
        List<PlayerRank> result = new ArrayList<>();
        for (PlayerRank pr : playerRanks.values()) {
            if (pr.isActive() && pr.hasRank(rankId)) {
                result.add(pr);
            }
        }
        return result;
    }

    // ==================== Expiry Management ====================

    public void checkAndRemoveExpiredRanks() {
        for (PlayerRank playerRank : playerRanks.values()) {
            if (playerRank.isExpired()) {
                playerRank.setActive(false);
            }
        }
        saveData();
    }

    public List<PlayerRank> getExpiringRanks(long withinMillis) {
        long now = System.currentTimeMillis();
        List<PlayerRank> result = new ArrayList<>();
        
        for (PlayerRank pr : playerRanks.values()) {
            if (pr.isActive() && pr.getExpiresAt() > 0) {
                long timeToExpiry = pr.getExpiresAt() - now;
                if (timeToExpiry > 0 && timeToExpiry <= withinMillis) {
                    result.add(pr);
                }
            }
        }
        
        return result;
    }

    // ==================== Statistics ====================

    public int getTotalPlayersWithRank() {
        return (int) playerRanks.values().stream()
                .filter(PlayerRank::isActive)
                .count();
    }

    public Map<String, Integer> getRankDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        
        for (PlayerRank pr : playerRanks.values()) {
            if (pr.isActive()) {
                String rankId = pr.getPrimaryRank();
                distribution.put(rankId, distribution.getOrDefault(rankId, 0) + 1);
            }
        }
        
        return distribution;
    }
}
