package com.webx.dashboard.config.models;

import java.util.HashMap;
import java.util.Map;

public class DeathMessageConfig {
    public boolean enabled = true;
    public boolean broadcast = true;
    public boolean showLocation = true;
    public boolean showWorld = false;
    public String messageFormat = "§c☠ §7%player% §cdied";
    
    public Map<String, String> causes = new HashMap<>() {{
        put("pvp", "§c☠ §7%player% §cwas killed by §7%killer%");
        put("fall", "§c☠ §7%player% §cfell to their death");
        put("drowning", "§c☠ §7%player% §cdrowned");
        put("fire", "§c☠ §7%player% §cburned to death");
        put("lava", "§c☠ §7%player% §ctried to swim in lava");
        put("void", "§c☠ §7%player% §cfell into the void");
        put("default", "§c☠ §7%player% §cdied from %cause%");
    }};
    
    public DeathMessageConfig() {}
}
