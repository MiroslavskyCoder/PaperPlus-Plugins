package com.webx.warps.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Warp {
    private final String name;
    private final Location location;
    private final UUID creator;
    private final long createdAt;
    private String permission;
    private double cost;
    private String icon;
    private String description;
    private boolean enabled;
    private int usageCount;

    public Warp(String name, Location location, UUID creator) {
        this.name = name;
        this.location = location;
        this.creator = creator;
        this.createdAt = System.currentTimeMillis();
        this.enabled = true;
        this.cost = 0.0;
        this.usageCount = 0;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location.clone();
    }

    public UUID getCreator() {
        return creator;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    public boolean canUse(Player player) {
        if (!enabled) return false;
        if (permission != null && !player.hasPermission(permission)) return false;
        return true;
    }
}
