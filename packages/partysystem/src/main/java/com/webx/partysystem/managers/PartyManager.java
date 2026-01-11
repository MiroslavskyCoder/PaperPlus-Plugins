package com.webx.partysystem.managers;

import com.webx.partysystem.models.Party;
import org.bukkit.entity.Player;
import java.util.*;

public class PartyManager {
    private final Map<UUID, Party> playerParties = new HashMap<>();
    private final Map<String, Party> partyMap = new HashMap<>();
    
    public Party createParty(Player leader) {
        Party party = new Party(UUID.randomUUID(), leader.getUniqueId(), leader.getName());
        partyMap.put(party.getPartyId().toString(), party);
        playerParties.put(leader.getUniqueId(), party);
        return party;
    }
    
    public void addMember(Player player, Party party) {
        playerParties.put(player.getUniqueId(), party);
        party.addMember(player.getUniqueId());
    }
}
