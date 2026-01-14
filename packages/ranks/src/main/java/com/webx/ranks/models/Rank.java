package com.webx.ranks.models;

import java.util.*;

/**
 * Represents a player rank with permissions, prefix/suffix, and pricing
 */
public class Rank {
    private String id;                    // Unique identifier: "vip", "premium", "gold"
    private String displayName;           // Display name: "VIP", "Premium Member"
    private int priority;                 // Priority for prefix display (higher = more important)
    private String prefix;                // Prefix in chat: "&b[VIP]&r"
    private String suffix;                // Suffix in chat: "&r&7(VIP)"
    private Set<String> permissions;      // Permission nodes: "rank.vip.fly", "rank.vip.teleport"
    private long purchasePrice;           // Price in coins (0 = not purchasable)
    private long minPlaytimeHours;        // Minimum playtime to receive (0 = not available)
    private String tabListFormat;         // Tab list format: "&b[VIP] &r%player_name%"
    private String chatFormat;            // Chat message format: "&b[VIP] %player_name%&r: %message%"
    private Map<String, Boolean> features; // Feature toggles: fly, increased-slots, etc.
    private boolean purchasable;          // Can be purchased from shop
    private boolean assignable;           // Can be assigned by admin
    private long createdAt;
    private long updatedAt;

    // Constructors
    public Rank() {
        this.permissions = new HashSet<>();
        this.features = new HashMap<>();
    }

    public Rank(String id, String displayName, int priority) {
        this();
        this.id = id;
        this.displayName = displayName;
        this.priority = priority;
        this.purchasable = false;
        this.assignable = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { 
        this.displayName = displayName;
        this.updatedAt = System.currentTimeMillis();
    }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { 
        this.priority = priority;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getPrefix() { return prefix != null ? prefix : ""; }
    public void setPrefix(String prefix) { 
        this.prefix = prefix;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getSuffix() { return suffix != null ? suffix : ""; }
    public void setSuffix(String suffix) { 
        this.suffix = suffix;
        this.updatedAt = System.currentTimeMillis();
    }

    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { 
        this.permissions = permissions;
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission) || permissions.contains("*");
    }

    public void addPermission(String permission) {
        permissions.add(permission);
        this.updatedAt = System.currentTimeMillis();
    }

    public void removePermission(String permission) {
        permissions.remove(permission);
        this.updatedAt = System.currentTimeMillis();
    }

    public long getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(long purchasePrice) { 
        this.purchasePrice = purchasePrice;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getMinPlaytimeHours() { return minPlaytimeHours; }
    public void setMinPlaytimeHours(long minPlaytimeHours) { 
        this.minPlaytimeHours = minPlaytimeHours;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getTabListFormat() { return tabListFormat != null ? tabListFormat : ""; }
    public void setTabListFormat(String tabListFormat) { 
        this.tabListFormat = tabListFormat;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getChatFormat() { return chatFormat != null ? chatFormat : ""; }
    public void setChatFormat(String chatFormat) { 
        this.chatFormat = chatFormat;
        this.updatedAt = System.currentTimeMillis();
    }

    public Map<String, Boolean> getFeatures() { 
        if (features == null) features = new HashMap<>();
        return features; 
    }
    public void setFeatures(Map<String, Boolean> features) { 
        this.features = features;
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean hasFeature(String feature) {
        return getFeatures().getOrDefault(feature, false);
    }

    public void setFeature(String feature, boolean enabled) {
        getFeatures().put(feature, enabled);
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean isPurchasable() { return purchasable; }
    public void setPurchasable(boolean purchasable) { 
        this.purchasable = purchasable;
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean isAssignable() { return assignable; }
    public void setAssignable(boolean assignable) { 
        this.assignable = assignable;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
}
