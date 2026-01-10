package com.webx.miningevents.managers;

import com.webx.miningevents.models.MineEvent;
import java.util.*;

public class MiningEventManager {
    private final Map<String, MineEvent> events = new HashMap<>();
    
    public void registerEvent(String name, MineEvent event) {
        events.put(name, event);
    }
    
    public MineEvent getEvent(String name) {
        return events.get(name);
    }
}
