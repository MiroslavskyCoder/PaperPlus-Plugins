package com.webx.partysystem.utils;

import java.util.UUID;

public class PartyUtils {
    
    public static String getPartyRoleDisplay(UUID uuid, UUID leaderId) {
        return uuid.equals(leaderId) ? "§cLeader§r" : "§7Member§r";
    }
}
