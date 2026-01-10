package com.webx.seasons.managers;

import com.webx.seasons.models.Season;
import java.util.*;

public class SeasonManager {
    private final Map<String, Season> seasons = new HashMap<>();
    private Season currentSeason;
    
    public void registerSeason(Season season) {
        seasons.put(season.getName(), season);
    }
    
    public Season getCurrentSeason() {
        return currentSeason;
    }
    
    public void setCurrentSeason(String name) {
        this.currentSeason = seasons.get(name);
    }
}
