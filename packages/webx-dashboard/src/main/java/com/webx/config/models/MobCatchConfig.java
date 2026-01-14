package com.webx.dashboard.config.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobCatchConfig {
    public boolean enabled = true;
    public boolean requirePermission = true;
    public String permission = "mobcatch.use";
    
    public List<String> allowedMobs = Arrays.asList(
        "COW", "PIG", "SHEEP", "CHICKEN", "HORSE", "WOLF", "CAT", "VILLAGER"
    );
    
    public List<String> blacklistedMobs = Arrays.asList(
        "ENDER_DRAGON", "WITHER"
    );
    
    public Map<String, String> messages = new HashMap<>() {{
        put("caught", "§aMob caught! Right-click to release.");
        put("released", "§aMob released!");
        put("cannot-catch", "§cYou cannot catch this mob!");
        put("no-permission", "§cYou don't have permission to catch mobs!");
        put("inventory-full", "§cYour inventory is full!");
    }};
    
    public String eggName = "§6Captured %mob%";
    
    public MobCatchConfig() {}
}
