package com.webx.seasons.utils;

import com.webx.seasons.models.Season;

public class SeasonUtils {
    
    public static String getSeasonEmoji(Season season) {
        String name = season.getName().toLowerCase();
        if (name.contains("spring")) return "🌸";
        if (name.contains("summer")) return "☀️";
        if (name.contains("fall")) return "🍂";
        if (name.contains("winter")) return "❄️";
        return "⭐";
    }
}
