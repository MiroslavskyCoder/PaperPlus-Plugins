package com.webx.clans.models;

import java.util.*;

public class Clan {
    private final String name;
    private final UUID leader;
    private final Map<UUID, String> members; // UUID to Rank
    private int level;
    private long experience;
    private String description;
    private long createdAt;

    public Clan(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.members = new HashMap<>();
        this.level = 1;
        this.experience = 0;
        this.description = "A clan";
        this.createdAt = System.currentTimeMillis();
        this.members.put(leader, "LEADER");
    }

    public String getName() {
        return name;
    }

    public UUID getLeader() {
        return leader;
    }

    public void addMember(UUID uuid, String rank) {
        members.put(uuid, rank);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public Collection<UUID> getMembers() {
        return members.keySet();
    }

    public String getMemberRank(UUID uuid) {
        return members.getOrDefault(uuid, null);
    }

    public int getMemberCount() {
        return members.size();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.min(level, 5);
    }

    public long getExperience() {
        return experience;
    }

    public void addExperience(long amount) {
        this.experience += amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
