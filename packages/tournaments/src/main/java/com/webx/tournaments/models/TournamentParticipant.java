package com.webx.tournaments.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TournamentParticipant {
    private final UUID playerId;
    private final String playerName;
    private final LocalDateTime joinedAt;
    private int wins;
    private int losses;
    
    public TournamentParticipant(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.joinedAt = LocalDateTime.now();
        this.wins = 0;
        this.losses = 0;
    }
    
    public UUID getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public void addWin() { wins++; }
    public void addLoss() { losses++; }
}
