package com.webx.bedwarsevent.models;

import java.util.UUID;

public class BedWarsGame {
    private final UUID gameId;
    private boolean isActive;
    
    public BedWarsGame(UUID gameId) {
        this.gameId = gameId;
        this.isActive = true;
    }
    
    public UUID getGameId() { return gameId; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}
