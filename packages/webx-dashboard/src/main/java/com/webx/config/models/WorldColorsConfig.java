package com.webx.dashboard.config.models;

import java.util.HashMap;
import java.util.Map;

public class WorldColorsConfig {
    public boolean enabled = true;
    public Map<String, String> worldColors = new HashMap<>() {{
        put("world", "GREEN");
        put("world_nether", "RED");
        put("world_the_end", "LIGHT_PURPLE");
    }};
    public int updateInterval = 20;
    
    public WorldColorsConfig() {}
}
