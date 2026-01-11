package com.webx.dashboard.config.models;

import java.util.HashMap;
import java.util.Map;

public class SimpleHealConfig {
    public boolean healHealth = true;
    public boolean healFood = true;
    public boolean clearEffects = true;
    public boolean clearFire = true;
    public int cooldown = 60;
    
    public Map<String, String> messages = new HashMap<>() {{
        put("healed-self", "§aYou have been healed!");
        put("healed-other", "§a%player% has been healed!");
        put("healed-by", "§aYou have been healed by %healer%!");
        put("healed-all", "§aAll players have been healed!");
        put("player-not-found", "§cPlayer not found!");
        put("no-permission", "§cYou don't have permission to use this command!");
    }};
    
    public SimpleHealConfig() {}
}
