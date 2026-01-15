package com.webx.ranks.managers;

import com.webx.ranks.models.Rank;
import com.webx.ranks.models.Permission;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Manages all rank definitions and their permissions
 */
public class RankManager {
    private final File dataFolder;
    private File ranksFile;
    private File permissionsFile;
    private Map<String, Rank> ranks;
    private Set<Permission> permissions;
    private final Gson gson;

    public RankManager(File dataFolder) {
        this.dataFolder = dataFolder;
        this.ranksFile = new File(dataFolder, "ranks.json");
        this.permissionsFile = new File(dataFolder, "permissions.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.ranks = new HashMap<>();
        this.permissions = new HashSet<>();
        
        loadData();
    }

    // ==================== Loading & Saving ====================
    
    public void loadData() {
        loadRanks();
        loadPermissions();
    }

    private void loadRanks() {
        if (!ranksFile.exists()) {
            ranks = new HashMap<>();
            return;
        }

        try (Reader reader = new FileReader(ranksFile)) {
            // Новый формат: массив объектов, каждый объект: { "RANK": { "permissions": [ { "name": ..., "enable": ... }, ... ] } }
            Type listType = new TypeToken<List<Map<String, Map<String, Object>>>>() {}.getType();
            List<Map<String, Map<String, Object>>> rankList = gson.fromJson(reader, listType);
            ranks = new HashMap<>();
            if (rankList != null) {
                for (Map<String, Map<String, Object>> rankObj : rankList) {
                    for (Map.Entry<String, Map<String, Object>> entry : rankObj.entrySet()) {
                        String rankId = entry.getKey();
                        Map<String, Object> rankData = entry.getValue();
                        Rank rank = new Rank();
                        rank.setId(rankId);
                        // Можно добавить другие поля, если есть (например, displayName, priority и т.д.)
                        if (rankData.containsKey("displayName")) {
                            rank.setDisplayName((String) rankData.get("displayName"));
                        }
                        if (rankData.containsKey("priority")) {
                            Number prio = (Number) rankData.get("priority");
                            rank.setPriority(prio != null ? prio.intValue() : 0);
                        }
                        if (rankData.containsKey("parent")) {
                            rank.setParent((String) rankData.get("parent"));
                        }
                        if (rankData.containsKey("color")) {
                            rank.setColor((String) rankData.get("color"));
                        }
                        if (rankData.containsKey("tabColor")) {
                            rank.setTabColor((String) rankData.get("tabColor"));
                        }
                        if (rankData.containsKey("chatColor")) {
                            rank.setChatColor((String) rankData.get("chatColor"));
                        }
                        if (rankData.containsKey("temporary")) {
                            Object tempObj = rankData.get("temporary");
                            boolean temp = tempObj instanceof Boolean ? (Boolean) tempObj : Boolean.parseBoolean(String.valueOf(tempObj));
                            rank.setTemporary(temp);
                        }
                        if (rankData.containsKey("expireAt")) {
                            Object expObj = rankData.get("expireAt");
                            long exp = expObj instanceof Number ? ((Number) expObj).longValue() : Long.parseLong(String.valueOf(expObj));
                            rank.setExpireAt(exp);
                        }
                        // Парсим permissions
                        Set<String> perms = new HashSet<>();
                        if (rankData.containsKey("permissions")) {
                            List<?> permsRaw = (List<?>) rankData.get("permissions");
                            for (Object permObjRaw : permsRaw) {
                                if (permObjRaw instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> permObj = (Map<String, Object>) permObjRaw;
                                    String permName = (String) permObj.get("name");
                                    Boolean enabled = permObj.get("enable") instanceof Boolean ? (Boolean) permObj.get("enable") : Boolean.valueOf(String.valueOf(permObj.get("enable")));
                                    if (permName != null && enabled != null && enabled) {
                                        perms.add(permName);
                                    }
                                }
                            }
                        }
                        rank.setPermissions(perms);
                        ranks.put(rankId, rank);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load ranks: " + e.getMessage());
            ranks = new HashMap<>();
        }
    }

    private void loadPermissions() {
        if (!permissionsFile.exists()) {
            permissions = new HashSet<>();
            return;
        }
        try (Reader reader = new FileReader(permissionsFile)) {
            Type type = new TypeToken<HashSet<Permission>>() {}.getType();
            permissions = gson.fromJson(reader, type);
            if (permissions == null) permissions = new HashSet<>();
        } catch (Exception e) {
            System.err.println("Failed to load permissions: " + e.getMessage());
            permissions = new HashSet<>();
        }
    }

    public void saveData() {
        saveRanks();
        savePermissions();
    }

    public void saveRanks() {
        try {
            dataFolder.mkdirs();
            try (Writer writer = new FileWriter(ranksFile)) {
                gson.toJson(ranks, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to save ranks: " + e.getMessage());
        }
    }

    public void savePermissions() {
        try {
            dataFolder.mkdirs();
            try (Writer writer = new FileWriter(permissionsFile)) {
                gson.toJson(permissions, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to save permissions: " + e.getMessage());
        }
    } 

    public boolean deleteRank(String rankId) {
        if (rankId.equals("member") || rankId.equals("founder")) {
            return false; // Cannot delete default ranks
        }
        
        if (ranks.remove(rankId) != null) {
            saveRanks();
            return true;
        }
        return false;
    }

    // ==================== Permission Management ====================

    public void registerPermission(String node, String description, String category) {
        Permission perm = new Permission(node, description, category);
        permissions.add(perm);
        savePermissions();
    }

    public Permission getPermission(String node) {
        return permissions.stream()
                .filter(p -> p.getNode().equals(node))
                .findFirst()
                .orElse(null);
    }

    public Set<Permission> getAllPermissions() {
        return new HashSet<>(permissions);
    }

    public Set<Permission> getPermissionsByCategory(String category) {
        Set<Permission> result = new HashSet<>();
        for (Permission perm : permissions) {
            if (category.equals(perm.getCategory())) {
                result.add(perm);
            }
        }
        return result;
    }

    public void initializeDefaultPermissions() {
        // Movement permissions
        registerPermission("rank.vip.fly", "Allow flying with fly command", "movement");
        registerPermission("rank.vip.speed", "Increased movement speed", "movement");
        registerPermission("rank.vip.teleport", "Access to special teleport commands", "movement");
        
        // Gameplay permissions
        registerPermission("rank.vip.extended-slots", "Additional inventory/clan slots", "gameplay");
        registerPermission("rank.vip.priority-join", "Priority join when server is full", "gameplay");
        registerPermission("rank.premium.custom-prefix", "Custom chat prefix", "gameplay");
        registerPermission("rank.premium.particle-effects", "Access to cosmetic particles", "gameplay");
        
        // Social permissions
        registerPermission("rank.vip.bypass-afk", "Not kicked for AFK", "social");
        registerPermission("rank.vip.global-chat", "Access to global chat channels", "social");
        
        // Economy permissions
        registerPermission("rank.vip.market-discount", "Reduced prices in marketplace", "economy");
        registerPermission("rank.premium.market-commission", "Lower marketplace commission", "economy");
        
        // Admin permissions
        registerPermission("rank.admin.manage-ranks", "Can assign/remove ranks", "admin");
        registerPermission("rank.admin.edit-ranks", "Can edit rank configuration", "admin");
        registerPermission("rank.admin.view-logs", "Can view admin logs", "admin");
    }

    // ==================== Utility ====================

    public boolean hasPermission(Rank rank, String permissionNode) {
        return rank.hasPermission(permissionNode);
    }

    public String formatPrefix(Rank rank) {
        String prefix = rank.getPrefix();
        return prefix.replace("&", "§");
    }

    public String formatTabListName(String playerName, Rank rank) {
        String format = rank.getTabListFormat();
        if (format.isEmpty()) {
            return formatPrefix(rank) + playerName;
        }
        return format
                .replace("&", "§")
                .replace("%player_name%", playerName);
    }
}
