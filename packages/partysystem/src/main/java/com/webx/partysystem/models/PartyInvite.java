package com.webx.partysystem.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class PartyInvite {
    private final UUID invitedUuid;
    private final UUID partyId;
    private final LocalDateTime createdAt;
    private boolean accepted;
    
    public PartyInvite(UUID invitedUuid, UUID partyId) {
        this.invitedUuid = invitedUuid;
        this.partyId = partyId;
        this.createdAt = LocalDateTime.now();
        this.accepted = false;
    }
    
    public UUID getInvitedUuid() { return invitedUuid; }
    public UUID getPartyId() { return partyId; }
    public boolean isAccepted() { return accepted; }
    public void accept() { this.accepted = true; }
}
