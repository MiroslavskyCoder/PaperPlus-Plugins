package com.webx.pvpbase;

public enum GameMode {
    SKYWARS("SkyWars", "Last player/team standing wins on their island"),
    BEDWARS("BedWars", "Destroy enemy beds, collect resources"),
    DUELS("Duels", "1v1 or small arena fights, winner gets points"),
    SIEGE("Siege", "Territory control, strategy and raids");

    private final String displayName;
    private final String description;

    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
