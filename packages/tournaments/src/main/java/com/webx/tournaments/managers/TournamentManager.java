package com.webx.tournaments.managers;

import com.webx.tournaments.models.Tournament;
import java.util.*;

public class TournamentManager {
    private final Map<String, Tournament> tournaments = new HashMap<>();
    
    public void registerTournament(String name, Tournament tournament) {
        tournaments.put(name, tournament);
    }
    
    public Tournament getTournament(String name) {
        return tournaments.get(name);
    }
}
