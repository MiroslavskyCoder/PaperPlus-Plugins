package com.webx.regionigroks;

import org.bukkit.Color;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Region {
    public enum Privacy { OPEN, CLOSED }

    private final String name;
    private final Color color;
    private final UUID owner;

    // Geometry: center X/Z and radius in blocks; world as UUID string
    private final String worldId;
    private final int centerX;
    private final int centerZ;
    private final int radius;

    // Membership and invites
    private Privacy privacy = Privacy.OPEN;
    private final Set<UUID> members = new HashSet<>();
    private final Set<UUID> invites = new HashSet<>();

    public Region(String name, Color color, UUID owner, String worldId, int centerX, int centerZ, int radius) {
        this.name = name;
        this.color = color;
        this.owner = owner;
        this.worldId = worldId;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
        this.members.add(owner);
    }

    public String getName() { return name; }
    public Color getColor() { return color; }
    public UUID getOwner() { return owner; }
    public Privacy getPrivacy() { return privacy; }
    public void setPrivacy(Privacy privacy) { this.privacy = privacy; }
    public Set<UUID> getMembers() { return members; }
    public Set<UUID> getInvites() { return invites; }
    public int getRadius() { return radius; }
    public int getCenterX() { return centerX; }
    public int getCenterZ() { return centerZ; }
    public String getWorldId() { return worldId; }

    public boolean contains(Location loc) {
        if (loc.getWorld() == null) return false;
        String wid = loc.getWorld().getUID().toString();
        if (!wid.equals(this.worldId)) return false;
        int dx = loc.getBlockX() - centerX;
        int dz = loc.getBlockZ() - centerZ;
        return (dx * dx + dz * dz) <= (radius * radius);
    }

    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }

    public boolean isInvited(UUID playerId) {
        return invites.contains(playerId);
    }

    public void addMember(UUID playerId) {
        members.add(playerId);
    }

    public void removeMember(UUID playerId) {
        members.remove(playerId);
    }

    public void invite(UUID playerId) { invites.add(playerId); }
    public void revokeInvite(UUID playerId) { invites.remove(playerId); }
}
