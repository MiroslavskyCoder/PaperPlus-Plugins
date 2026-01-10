package com.webx.claims.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class ClaimOwner {
    private final UUID ownerId;
    private final String ownerName;
    private final LocalDateTime createdAt;
    
    public ClaimOwner(UUID ownerId, String ownerName) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdAt = LocalDateTime.now();
    }
    
    public UUID getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
