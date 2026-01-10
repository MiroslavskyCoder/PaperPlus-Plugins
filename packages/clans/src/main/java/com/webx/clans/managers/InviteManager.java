package com.webx.clans.managers;

import com.webx.clans.ClansPlugin;

import java.util.*;

public class InviteManager {
    private final ClansPlugin plugin;
    private final Map<UUID, String> invites; // playerUUID -> clanName

    public InviteManager(ClansPlugin plugin) {
        this.plugin = plugin;
        this.invites = new HashMap<>();
    }

    public void invitePlayer(UUID uuid, String clanName) {
        invites.put(uuid, clanName);
    }

    public String getInvite(UUID uuid) {
        return invites.get(uuid);
    }

    public void removeInvite(UUID uuid) {
        invites.remove(uuid);
    }

    public boolean hasInvite(UUID uuid) {
        return invites.containsKey(uuid);
    }
}
