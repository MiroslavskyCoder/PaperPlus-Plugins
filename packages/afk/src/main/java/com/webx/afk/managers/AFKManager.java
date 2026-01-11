package com.webx.afk.managers;

import com.webx.afk.models.AFKPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class AFKManager {
    private final Map<UUID, AFKPlayer> afkPlayers = new HashMap<>();
    private final long afkTimeoutMillis;
    
    public AFKManager(long afkTimeoutMillis) {
        this.afkTimeoutMillis = afkTimeoutMillis;
    }
    
    public void updateActivity(UUID uuid) {
        afkPlayers.computeIfAbsent(uuid, k -> new AFKPlayer(uuid)).updateActivity();
    }
    
    public void setAFK(UUID uuid, boolean afk) {
        AFKPlayer player = afkPlayers.get(uuid);
        if (player != null) {
            player.setAFK(afk);
        }
    }
    
    public boolean isAFK(UUID uuid) {
        AFKPlayer player = afkPlayers.get(uuid);
        return player != null && player.isAFK();
    }
    
    public Map<UUID, AFKPlayer> getAFKPlayers() {
        return new HashMap<>(afkPlayers);
    }
    
    public void checkAFKStatus() {
        long now = System.currentTimeMillis();
        
        for (AFKPlayer afkPlayer : afkPlayers.values()) {
            if (!afkPlayer.isAFK() && now - afkPlayer.getLastActivity() > afkTimeoutMillis) {
                afkPlayer.setAFK(true);
            }
        }
    }
    
    public void removePlayer(UUID uuid) {
        afkPlayers.remove(uuid);
    }
    
    public int getAFKCount() {
        return (int) afkPlayers.values().stream().filter(AFKPlayer::isAFK).count();
    }
}
