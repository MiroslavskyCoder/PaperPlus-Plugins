package com.webx.dashboard.config.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendFeedConfig {
    public boolean enabled = true;
    public boolean requirePermission = false;
    public String permission = "friendfeed.use";
    public int restoreHunger = 10;
    public double restoreSaturation = 5.0;
    public boolean giveEffects = true;
    public int cooldown = 30;
    public double range = 5.0;
    
    public List<Effect> effects = new ArrayList<>() {{
        add(new Effect("REGENERATION", 100, 0));
        add(new Effect("ABSORPTION", 200, 0));
    }};
    
    public Map<String, String> messages = new HashMap<>() {{
        put("fed-player", "§aYou fed %player%!");
        put("fed-by", "§aYou were fed by %feeder%!");
        put("too-far", "§cPlayer is too far away!");
        put("on-cooldown", "§cCooldown: %time% seconds");
        put("no-permission", "§cYou don't have permission!");
        put("player-full", "§c%player% is not hungry!");
    }};
    
    public static class Effect {
        public String type;
        public int duration;
        public int amplifier;
        
        public Effect() {}
        
        public Effect(String type, int duration, int amplifier) {
            this.type = type;
            this.duration = duration;
            this.amplifier = amplifier;
        }
    }
    
    public FriendFeedConfig() {}
}
