package com.webx.pvpbase;

import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.*;

public class MatchManager {
    private final Map<UUID, Match> activeMatches = new HashMap<>();

    public Match createMatch(GameMode gameMode, World world, String arenaName) {
        UUID id = UUID.randomUUID();
        Match match = new Match(id, gameMode, world, arenaName);
        activeMatches.put(id, match);
        return match;
    }

    public void deleteMatch(UUID matchId) {
        activeMatches.remove(matchId);
    }

    public Match getMatch(UUID matchId) {
        return activeMatches.get(matchId);
    }

    public List<Match> getActiveMatches() {
        return new ArrayList<>(activeMatches.values());
    }

    public Match getMatchByPlayer(UUID playerId) {
        return activeMatches.values().stream()
            .filter(m -> m.getPlayerClasses().containsKey(playerId))
            .findFirst()
            .orElse(null);
    }

    public Match getMatchByWorld(World world) {
        return activeMatches.values().stream()
            .filter(m -> m.getWorld().equals(world))
            .findFirst()
            .orElse(null);
    }
}
