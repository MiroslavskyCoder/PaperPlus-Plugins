package com.webx.seasons;
import com.webx.seasons.managers.SeasonManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SeasonsPlugin extends JavaPlugin {
    private static SeasonsPlugin instance;
    private Season currentSeason;
    private SeasonManager seasonManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
            seasonManager = new SeasonManager();
        
        currentSeason = Season.SPRING;
        
        // Смена сезонов каждые 7 дней (7*24*60*60 = 604800 тиков)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            currentSeason = currentSeason.next();
            Bukkit.broadcastMessage("§6Season changed to: §f" + currentSeason.name());
        }, 604800, 604800);
        
        getLogger().info("Seasons Plugin enabled!");
    }
    
    public Season getCurrentSeason() {
        return currentSeason;
    }
    
    public static SeasonsPlugin getInstance() {
        return instance;
    
        public SeasonManager getSeasonManager() {
            return seasonManager;
        }
    }
    
    public enum Season {
        SPRING, SUMMER, AUTUMN, WINTER;
        
        public Season next() {
            switch (this) {
                case SPRING: return SUMMER;
                case SUMMER: return AUTUMN;
                case AUTUMN: return WINTER;
                case WINTER: return SPRING;
                default: return SPRING;
            }
        }
    }
}
