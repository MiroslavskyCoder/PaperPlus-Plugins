package com.webx.guilds.models;

import java.util.*;

public class Guild {
    private final UUID id;
    private final String name;
    private final String leader;
    private final Set<String> members = new HashSet<>();
    
    public Guild(UUID id, String name, String leader) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.members.add(leader);
    }
    
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getLeader() { return leader; }
    public void addMember(String member) { members.add(member); }
}
