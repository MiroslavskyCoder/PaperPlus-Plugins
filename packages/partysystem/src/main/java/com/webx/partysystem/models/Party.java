package com.webx.partysystem.models;

import java.util.*;

public class Party {
    private final UUID partyId;
    private final UUID leaderId;
    private final String partyName;
    private final Set<UUID> members = new HashSet<>();
    
    public Party(UUID partyId, UUID leaderId, String partyName) {
        this.partyId = partyId;
        this.leaderId = leaderId;
        this.partyName = partyName;
        this.members.add(leaderId);
    }
    
    public void addMember(UUID uuid) { members.add(uuid); }
    public void removeMember(UUID uuid) { members.remove(uuid); }
    
    public UUID getPartyId() { return partyId; }
    public UUID getLeaderId() { return leaderId; }
    public Set<UUID> getMembers() { return new HashSet<>(members); }
}
