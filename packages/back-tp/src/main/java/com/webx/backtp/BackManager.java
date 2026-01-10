package com.webx.backtp;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackManager {
    private final Map<UUID, Location> deathLocations = new HashMap<>();

    public void setDeathLocation(Player player, Location location) {
        deathLocations.put(player.getUniqueId(), location.clone());
    }

    public Location getDeathLocation(Player player) {
        return deathLocations.get(player.getUniqueId());
    }

    public boolean hasDeathLocation(Player player) {
        return deathLocations.containsKey(player.getUniqueId());
    }
}
