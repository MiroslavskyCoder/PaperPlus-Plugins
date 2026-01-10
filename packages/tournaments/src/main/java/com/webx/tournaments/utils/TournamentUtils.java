package com.webx.tournaments.utils;

public class TournamentUtils {
    
    public static String getTournamentStatus(boolean isActive) {
        return isActive ? "§aACTIVE" : "§cINACTIVE";
    }
    
    public static int calculatePrizePool(int participants) {
        return participants * 100;
    }
}
