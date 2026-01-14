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
    private final File ranksFile;
    private final File permissionsFile;
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
        initializeDefaultRanks();
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
            Type type = new TypeToken<Map<String, Rank>>() {}.getType();
            ranks = gson.fromJson(reader, type);
            if (ranks == null) ranks = new HashMap<>();
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

    // ==================== Rank Management ====================

    private void initializeDefaultRanks() {
        if (ranks.isEmpty()) {
            // Default Member rank
            Rank member = new Rank("member", "Member", 1);
            member.setPrefix("&7[Member]&r");
            member.setAssignable(true);
            member.setPurchasable(false);
            ranks.put("member", member);

            // VIP Rank
            Rank vip = new Rank("vip", "VIP", 10);
            vip.setPrefix("&b[VIP]&r");
            vip.setTabListFormat("&b[VIP] &r%player_name%");
            vip.setPurchasePrice(5000);
            vip.setPurchasable(true);
            vip.setAssignable(true);
            vip.addPermission("rank.vip.fly");
            vip.addPermission("rank.vip.extended-slots");
            vip.addPermission("rank.vip.priority-join");
            vip.setFeature("fly", true);
            vip.setFeature("extended-clan-slots", true);
            ranks.put("vip", vip);

            // Premium Rank
            Rank premium = new Rank("premium", "Premium", 20);
            premium.setPrefix("&6[PREMIUM]&r");
            premium.setTabListFormat("&6[PREMIUM] &r%player_name%");
            premium.setPurchasePrice(15000);
            premium.setPurchasable(true);
            premium.setAssignable(true);
            premium.addPermission("rank.vip.fly");
            premium.addPermission("rank.vip.extended-slots");
            premium.addPermission("rank.vip.priority-join");
            premium.addPermission("rank.premium.custom-prefix");
            premium.addPermission("rank.premium.particle-effects");
            premium.setFeature("fly", true);
            premium.setFeature("extended-clan-slots", true);
            premium.setFeature("custom-prefix", true);
            premium.setFeature("particle-effects", true);
            ranks.put("premium", premium);

            // Founder Rank
            Rank founder = new Rank("founder", "Founder", 100);
            founder.setPrefix("&c[FOUNDER]&r");
            founder.setTabListFormat("&c[FOUNDER] &r%player_name%");
            founder.setPurchasable(false);
            founder.setAssignable(true);
            founder.addPermission("*");
            founder.setFeature("fly", true);
            founder.setFeature("all-features", true);
            ranks.put("founder", founder);

            saveRanks();
        }
    }

    public Rank createRank(String id, String displayName, int priority) {
        if (ranks.containsKey(id)) return null;
        
        Rank rank = new Rank(id, displayName, priority);
        ranks.put(id, rank);
        saveRanks();
        return rank;
    }

    public Rank getRank(String rankId) {
        return ranks.get(rankId);
    }

    public Map<String, Rank> getAllRanks() {
        return new HashMap<>(ranks);
    }

    public List<Rank> getRanksSortedByPriority() {
        List<Rank> sortedRanks = new ArrayList<>(ranks.values());
        sortedRanks.sort((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority()));
        return sortedRanks;
    }

    public void updateRank(Rank rank) {
        if (rank.getId() != null) {
            ranks.put(rank.getId(), rank);
            saveRanks();
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
