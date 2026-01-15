package com.webx.ranks.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.UUID;

public class RankChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UUID player;
    private final String newRankId;
    public RankChangeEvent(UUID player, String newRankId) {
        this.player = player;
        this.newRankId = newRankId;
    }
    public UUID getPlayer() { return player; }
    public String getNewRankId() { return newRankId; }
    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
