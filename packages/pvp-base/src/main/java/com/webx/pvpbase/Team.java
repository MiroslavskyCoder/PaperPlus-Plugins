package com.webx.pvpbase;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Team {
    private final String name;
    private final ChatColor color;
    private final Map<UUID, Player> members = new HashMap<>();
    private boolean bedDestroyed = false; // for BedWars

    public Team(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public ChatColor getColor() { return color; }
    public Map<UUID, Player> getMembers() { return members; }
    public boolean isBedDestroyed() { return bedDestroyed; }
    public void setBedDestroyed(boolean destroyed) { this.bedDestroyed = destroyed; }

    public void addMember(Player player) {
        members.put(player.getUniqueId(), player);
    }

    public void removeMember(UUID playerId) {
        members.remove(playerId);
    }

    public boolean hasMember(UUID playerId) {
        return members.containsKey(playerId);
    }

    public int getAliveMembers() {
        return (int) members.values().stream().filter(p -> !p.isDead()).count();
    }
}
