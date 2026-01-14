package com.webx.ranks.models;

/**
 * Represents a permission node with metadata
 */
public class Permission {
    private String node;           // e.g., "rank.vip.fly", "rank.vip.teleport"
    private String description;    // Human-readable description
    private String category;       // e.g., "movement", "chat", "economy"
    private boolean isDefault;     // Whether all players have this by default

    // Constructors
    public Permission() {}

    public Permission(String node, String description, String category) {
        this.node = node;
        this.description = description;
        this.category = category;
        this.isDefault = false;
    }

    // Getters and Setters
    public String getNode() { return node; }
    public void setNode(String node) { this.node = node; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    @Override
    public String toString() {
        return "Permission{" +
                "node='" + node + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
