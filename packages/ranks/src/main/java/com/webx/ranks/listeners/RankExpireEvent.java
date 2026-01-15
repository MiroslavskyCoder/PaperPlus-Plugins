package com.webx.ranks.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.UUID;

public class RankExpireEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UUID player;
    private final String expiredRankId;
    public RankExpireEvent(UUID player, String expiredRankId) {
        this.player = player;
        this.expiredRankId = expiredRankId;
    }
    public UUID getPlayer() { return player; }
    public String getExpiredRankId() { return expiredRankId; }
    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
