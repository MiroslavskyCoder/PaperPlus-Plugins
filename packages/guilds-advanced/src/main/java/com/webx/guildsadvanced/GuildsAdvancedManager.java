package com.webx.guildsadvanced;

import org.bukkit.entity.Player;
import java.util.*;

public class GuildsAdvancedManager {
    private Map<String, AdvancedGuild> guilds = new HashMap<>();
    
    public void createGuild(String name, Player owner) {
        guilds.put(name, new AdvancedGuild(name, owner.getUniqueId()));
        owner.sendMessage("§aGuild §f" + name + " §acreated!");
    }
    
    public AdvancedGuild getGuild(String name) {
        return guilds.get(name);
    }
    
    static class AdvancedGuild {
        String name;
        UUID owner;
        List<UUID> members = new ArrayList<>();
        long createdAt;
        
        AdvancedGuild(String name, UUID owner) {
            this.name = name;
            this.owner = owner;
            this.createdAt = System.currentTimeMillis();
            this.members.add(owner);
        }
    }
}
