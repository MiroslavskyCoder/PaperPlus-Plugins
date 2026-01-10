package com.webx.clans.managers;

import com.webx.clans.ClansPlugin;

import java.util.UUID;

public class MemberManager {
    private final ClansPlugin plugin;

    public MemberManager(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void addMember(String clanName, UUID uuid, String rank) {
        var clan = plugin.getClanManager().getClan(clanName);
        if (clan != null) {
            clan.addMember(uuid, rank);
        }
    }

    public void removeMember(String clanName, UUID uuid) {
        var clan = plugin.getClanManager().getClan(clanName);
        if (clan != null) {
            clan.removeMember(uuid);
        }
    }

    public void setMemberRank(String clanName, UUID uuid, String rank) {
        var clan = plugin.getClanManager().getClan(clanName);
        if (clan != null) {
            clan.addMember(uuid, rank);
        }
    }
}
