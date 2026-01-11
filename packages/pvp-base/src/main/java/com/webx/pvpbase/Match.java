package com.webx.pvpbase;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.*;

public class Match {
    public enum State { LOBBY, ACTIVE, ENDED }

    private final UUID id;
    private final GameMode gameMode;
    private final World world;
    private final String arenaName;
    private final Map<UUID, Team> teams = new HashMap<>();
    private final Map<UUID, PlayerClass> playerClasses = new HashMap<>();
    private State state = State.LOBBY;
    private long createdAt;
    private long startedAt;

    public Match(UUID id, GameMode gameMode, World world, String arenaName) {
        this.id = id;
        this.gameMode = gameMode;
        this.world = world;
        this.arenaName = arenaName;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getId() { return id; }
    public GameMode getGameMode() { return gameMode; }
    public World getWorld() { return world; }
    public String getArenaName() { return arenaName; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public Map<UUID, Team> getTeams() { return teams; }
    public Map<UUID, PlayerClass> getPlayerClasses() { return playerClasses; }

    public void addTeam(Team team) {
        teams.put(UUID.randomUUID(), team);
    }

    public void addPlayer(Player player, Team team, PlayerClass playerClass) {
        team.addMember(player);
        playerClasses.put(player.getUniqueId(), playerClass);
    }

    public Team getPlayerTeam(UUID playerId) {
        return teams.values().stream()
            .filter(t -> t.hasMember(playerId))
            .findFirst()
            .orElse(null);
    }

    public PlayerClass getPlayerClass(UUID playerId) {
        return playerClasses.get(playerId);
    }

    public int getTotalPlayers() {
        return teams.values().stream().mapToInt(t -> t.getMembers().size()).sum();
    }

    public void start() {
        this.state = State.ACTIVE;
        this.startedAt = System.currentTimeMillis();
    }

    public void end() {
        this.state = State.ENDED;
    }

    public long getDurationSeconds() {
        long end = state == State.ENDED ? System.currentTimeMillis() : System.currentTimeMillis();
        return (end - startedAt) / 1000;
    }
}
