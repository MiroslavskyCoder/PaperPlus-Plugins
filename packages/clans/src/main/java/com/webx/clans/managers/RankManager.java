package com.webx.clans.managers;

import com.webx.clans.ClansPlugin;

public class RankManager {
    private final ClansPlugin plugin;

    public RankManager(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean canBreakBlocks(String rank) {
        return rank.equals("LEADER") || rank.equals("OFFICER") || rank.equals("MEMBER");
    }

    public boolean canManageMembers(String rank) {
        return rank.equals("LEADER") || rank.equals("OFFICER");
    }

    public boolean canManageRanks(String rank) {
        return rank.equals("LEADER");
    }

    public boolean canEditClan(String rank) {
        return rank.equals("LEADER") || rank.equals("OFFICER");
    }
}
