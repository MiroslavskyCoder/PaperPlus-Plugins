package com.webx.api.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Service for interacting with Ranks plugin via reflection
 */
public class RankService {
    private final Gson gson;
    private Plugin ranksPlugin;
    private Object rankManager;
    private Object playerRankManager;

    public RankService() {
        this.gson = new Gson();
        initializeRanksPlugin();
    }

    private void initializeRanksPlugin() {
        try {
            ranksPlugin = Bukkit.getPluginManager().getPlugin("Ranks");
            if (ranksPlugin != null && ranksPlugin.isEnabled()) {
                // Get managers via reflection
                Method getRankManagerMethod = ranksPlugin.getClass().getMethod("getRankManager");
                rankManager = getRankManagerMethod.invoke(ranksPlugin);

                Method getPlayerRankManagerMethod = ranksPlugin.getClass().getMethod("getPlayerRankManager");
                playerRankManager = getPlayerRankManagerMethod.invoke(ranksPlugin);
                
                System.out.println("✅ RankService initialized successfully");
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize RankService: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isAvailable() {
        return ranksPlugin != null && ranksPlugin.isEnabled() && rankManager != null && playerRankManager != null;
    }

    // ==================== Rank Management ====================

    public List<Map<String, Object>> getAllRanks() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Method getAllRanksMethod = rankManager.getClass().getMethod("getAllRanks");
            @SuppressWarnings("unchecked")
            Map<String, Object> ranks = (Map<String, Object>) getAllRanksMethod.invoke(rankManager);

            for (Map.Entry<String, Object> entry : ranks.entrySet()) {
                result.add(rankToMap(entry.getKey(), entry.getValue()));
            }
        } catch (Exception e) {
            System.err.println("Failed to get all ranks: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> getRank(String rankId) {
        try {
            Method getRankMethod = rankManager.getClass().getMethod("getRank", String.class);
            Object rank = getRankMethod.invoke(rankManager, rankId);
            
            if (rank != null) {
                return rankToMap(rankId, rank);
            }
        } catch (Exception e) {
            System.err.println("Failed to get rank: " + e.getMessage());
        }
        return null;
    }

    public boolean updateRank(String rankId, JsonObject updateData) {
        try {
            Method getRankMethod = rankManager.getClass().getMethod("getRank", String.class);
            Object rank = getRankMethod.invoke(rankManager, rankId);

            if (rank == null) return false;

            // Update rank properties
            if (updateData.has("displayName")) {
                Method setDisplayNameMethod = rank.getClass().getMethod("setDisplayName", String.class);
                setDisplayNameMethod.invoke(rank, updateData.get("displayName").getAsString());
            }

            if (updateData.has("priority")) {
                Method setPriorityMethod = rank.getClass().getMethod("setPriority", int.class);
                setPriorityMethod.invoke(rank, updateData.get("priority").getAsInt());
            }

            if (updateData.has("prefix")) {
                Method setPrefixMethod = rank.getClass().getMethod("setPrefix", String.class);
                setPrefixMethod.invoke(rank, updateData.get("prefix").getAsString());
            }

            if (updateData.has("suffix")) {
                Method setSuffixMethod = rank.getClass().getMethod("setSuffix", String.class);
                setSuffixMethod.invoke(rank, updateData.get("suffix").getAsString());
            }

            if (updateData.has("purchasePrice")) {
                Method setPurchasePriceMethod = rank.getClass().getMethod("setPurchasePrice", long.class);
                setPurchasePriceMethod.invoke(rank, updateData.get("purchasePrice").getAsLong());
            }

            if (updateData.has("purchasable")) {
                Method setPurchasableMethod = rank.getClass().getMethod("setPurchasable", boolean.class);
                setPurchasableMethod.invoke(rank, updateData.get("purchasable").getAsBoolean());
            }

            Method updateRankMethod = rankManager.getClass().getMethod("updateRank", rank.getClass());
            updateRankMethod.invoke(rankManager, rank);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update rank: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRank(String rankId) {
        try {
            Method deleteRankMethod = rankManager.getClass().getMethod("deleteRank", String.class);
            return (boolean) deleteRankMethod.invoke(rankManager, rankId);
        } catch (Exception e) {
            System.err.println("Failed to delete rank: " + e.getMessage());
            return false;
        }
    }

    // ==================== Player Rank Management ====================

    public Map<String, Object> getPlayerRank(String playerUUID) {
        try {
            UUID uuid = UUID.fromString(playerUUID);
            Method getPlayerRankMethod = playerRankManager.getClass().getMethod("getPlayerRank", UUID.class);
            Object playerRank = getPlayerRankMethod.invoke(playerRankManager, uuid);

            if (playerRank != null) {
                return playerRankToMap(playerRank);
            }
        } catch (Exception e) {
            System.err.println("Failed to get player rank: " + e.getMessage());
        }
        return null;
    }

    public boolean setPlayerRank(String playerUUID, String rankId, String assignedBy, String reason) {
        try {
            UUID uuid = UUID.fromString(playerUUID);
            Method setMethod = playerRankManager.getClass()
                    .getMethod("setPlayerPrimaryRank", UUID.class, String.class, String.class, String.class);
            setMethod.invoke(playerRankManager, uuid, rankId, assignedBy, reason);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to set player rank: " + e.getMessage());
            return false;
        }
    }

    public boolean removePlayerRank(String playerUUID) {
        try {
            UUID uuid = UUID.fromString(playerUUID);
            Method removeMethod = playerRankManager.getClass().getMethod("removePlayerRank", UUID.class);
            removeMethod.invoke(playerRankManager, uuid);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to remove player rank: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Integer> getRankDistribution() {
        try {
            Method getDistributionMethod = playerRankManager.getClass().getMethod("getRankDistribution");
            @SuppressWarnings("unchecked")
            Map<String, Integer> distribution = (Map<String, Integer>) getDistributionMethod.invoke(playerRankManager);
            return distribution != null ? distribution : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Failed to get rank distribution: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // ==================== Helper Methods ====================

    private Map<String, Object> rankToMap(String rankId, Object rank) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("id", rankId);
            map.put("displayName", getFieldValue(rank, "displayName"));
            map.put("priority", getFieldValue(rank, "priority"));
            map.put("prefix", getFieldValue(rank, "prefix"));
            map.put("suffix", getFieldValue(rank, "suffix"));
            map.put("purchasePrice", getFieldValue(rank, "purchasePrice"));
            map.put("purchasable", getFieldValue(rank, "purchasable"));
            map.put("assignable", getFieldValue(rank, "assignable"));
            map.put("permissions", getFieldValue(rank, "permissions"));
            map.put("features", getFieldValue(rank, "features"));
            map.put("createdAt", getFieldValue(rank, "createdAt"));
            map.put("updatedAt", getFieldValue(rank, "updatedAt"));
        } catch (Exception e) {
            System.err.println("Failed to convert rank to map: " + e.getMessage());
        }
        return map;
    }

    private Map<String, Object> playerRankToMap(Object playerRank) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("playerId", getFieldValue(playerRank, "playerId"));
            map.put("playerName", getFieldValue(playerRank, "playerName"));
            map.put("primaryRank", getFieldValue(playerRank, "primaryRank"));
            map.put("additionalRanks", getFieldValue(playerRank, "additionalRanks"));
            map.put("assignedAt", getFieldValue(playerRank, "assignedAt"));
            map.put("assignedBy", getFieldValue(playerRank, "assignedBy"));
            map.put("reason", getFieldValue(playerRank, "reason"));
            map.put("expiresAt", getFieldValue(playerRank, "expiresAt"));
            map.put("active", getFieldValue(playerRank, "active"));
            map.put("rankHistory", getFieldValue(playerRank, "rankHistory"));
        } catch (Exception e) {
            System.err.println("Failed to convert player rank to map: " + e.getMessage());
        }
        return map;
    }

    private Object getFieldValue(Object obj, String fieldName) throws Exception {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            // Try getter method
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                Method method = obj.getClass().getMethod(getterName);
                return method.invoke(obj);
            } catch (NoSuchMethodException e2) {
                return null;
            }
        }
    }
}
