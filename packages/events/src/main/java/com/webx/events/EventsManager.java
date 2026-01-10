package com.webx.events;

import org.bukkit.entity.Player;
import java.util.*;

public class EventsManager {
    private List<GameEvent> activeEvents = new ArrayList<>();
    private Map<UUID, Integer> playerParticipation = new HashMap<>();
    
    public void startEvent(GameEvent event) {
        activeEvents.add(event);
    }
    
    public void joinEvent(Player player, GameEvent event) {
        UUID uuid = player.getUniqueId();
        playerParticipation.put(uuid, playerParticipation.getOrDefault(uuid, 0) + 1);
        player.sendMessage("§aYou joined event: " + event.name);
    }
    
    public List<GameEvent> getActiveEvents() {
        return new ArrayList<>(activeEvents);
    }
    
    static class GameEvent {
        String name;
        String description;
        long startTime;
        
        GameEvent(String name, String description) {
            this.name = name;
            this.description = description;
            this.startTime = System.currentTimeMillis();
        }
    }
}
