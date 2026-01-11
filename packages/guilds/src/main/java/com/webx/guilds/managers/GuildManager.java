package com.webx.guilds.managers;

import com.webx.guilds.models.Guild;
import java.util.*;

public class GuildManager {
    private final Map<String, Guild> guilds = new HashMap<>();
    
    public void createGuild(String name, String leader) {
        guilds.put(name, new Guild(UUID.randomUUID(), name, leader));
    }
    
    public Guild getGuild(String name) {
        return guilds.get(name);
    }
    
    public Collection<Guild> getAllGuilds() {
        return guilds.values();
    }
}
