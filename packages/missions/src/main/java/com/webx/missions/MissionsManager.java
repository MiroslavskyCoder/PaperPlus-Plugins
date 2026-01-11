package com.webx.missions;

import org.bukkit.entity.Player;
import java.util.*;

public class MissionsManager {
    private Map<UUID, List<Mission>> activeMissions = new HashMap<>();
    
    public void assignMission(Player player, Mission mission) {
        UUID uuid = player.getUniqueId();
        activeMissions.computeIfAbsent(uuid, k -> new ArrayList<>()).add(mission);
        player.sendMessage("§6New mission: " + mission.name);
    }
    
    public List<Mission> getActiveMissions(Player player) {
        return activeMissions.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }
    
    public void completeMission(Player player, Mission mission) {
        activeMissions.getOrDefault(player.getUniqueId(), new ArrayList<>()).remove(mission);
        player.sendMessage("§6Mission completed!");
    }
    
    static class Mission {
        String name;
        String description;
        int reward;
        
        Mission(String name, String description, int reward) {
            this.name = name;
            this.description = description;
            this.reward = reward;
        }
    }
}
