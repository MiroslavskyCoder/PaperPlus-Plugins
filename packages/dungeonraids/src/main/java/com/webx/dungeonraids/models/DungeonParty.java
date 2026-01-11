package com.webx.dungeonraids.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DungeonParty {
    private final UUID partyLeader;
    private final Set<UUID> members = new HashSet<>();
    private final String dungeonName;
    
    public DungeonParty(UUID partyLeader, String dungeonName) {
        this.partyLeader = partyLeader;
        this.dungeonName = dungeonName;
        this.members.add(partyLeader);
    }
    
    public void addMember(UUID uuid) { members.add(uuid); }
    public UUID getPartyLeader() { return partyLeader; }
    public Set<UUID> getMembers() { return new HashSet<>(members); }
}
