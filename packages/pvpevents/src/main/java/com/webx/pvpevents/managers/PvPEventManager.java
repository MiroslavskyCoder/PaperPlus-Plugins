package com.webx.pvpevents.managers;

import com.webx.pvpevents.models.PvPEvent;
import java.util.*;

public class PvPEventManager {
    private final Map<String, PvPEvent> events = new HashMap<>();
    
    public void registerEvent(String name, PvPEvent event) {
        events.put(name, event);
    }
    
    public PvPEvent getEvent(String name) {
        return events.get(name);
    }
    
    public Collection<PvPEvent> getAllEvents() {
        return events.values();
    }
}
