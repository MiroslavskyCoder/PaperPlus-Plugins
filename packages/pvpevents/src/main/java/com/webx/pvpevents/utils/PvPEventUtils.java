package com.webx.pvpevents.utils;

import com.webx.pvpevents.models.PvPEvent;
import java.util.List;

public class PvPEventUtils {
    
    public static int countActiveEvents(List<PvPEvent> events) {
        return (int) events.stream().filter(PvPEvent::isActive).count();
    }
    
    public static String getEventStatus(PvPEvent event) {
        return event.isActive() ? "§aActive" : "§cInactive";
    }
}
