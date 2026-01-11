package com.webx.bedwarsevent.managers;

import com.webx.bedwarsevent.models.BedWarsGame;
import java.util.*;

public class BedWarsManager {
    private final List<BedWarsGame> games = new ArrayList<>();
    
    public void createGame() {
        games.add(new BedWarsGame(UUID.randomUUID()));
    }
    
    public List<BedWarsGame> getGames() {
        return new ArrayList<>(games);
    }
}
