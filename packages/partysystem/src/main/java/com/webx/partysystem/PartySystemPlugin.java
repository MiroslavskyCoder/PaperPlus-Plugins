package com.webx.partysystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PartySystemPlugin extends JavaPlugin implements Listener {
    private static PartySystemPlugin instance;
    private Map<UUID, Party> playerParties = new HashMap<>();
    private Map<UUID, Party> parties = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        getCommand("party").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                showPartyHelp(player);
                return true;
            }
            
            switch (args[0].toLowerCase()) {
                case "create":
                    createParty(player);
                    break;
                case "invite":
                    if (args.length > 1) inviteToParty(player, args[1]);
                    break;
                case "accept":
                    acceptPartyInvite(player);
                    break;
                case "leave":
                    leaveParty(player);
                    break;
                case "list":
                    listPartyMembers(player);
                    break;
            }
            
            return true;
        });
        
        getLogger().info("Party System Plugin enabled!");
    }
    
    private void createParty(Player leader) {
        if (playerParties.containsKey(leader.getUniqueId())) {
            leader.sendMessage("§cYou are already in a party!");
            return;
        }
        
        Party party = new Party(UUID.randomUUID(), leader.getUniqueId());
        parties.put(party.id, party);
        playerParties.put(leader.getUniqueId(), party);
        
        leader.sendMessage("§aParty created! Use /party invite <player> to invite members.");
    }
    
    private void inviteToParty(Player sender, String targetName) {
        Party party = playerParties.get(sender.getUniqueId());
        if (party == null || !party.leader.equals(sender.getUniqueId())) {
            sender.sendMessage("§cYou don't have a party or aren't the leader!");
            return;
        }
        
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        party.pendingInvites.put(target.getUniqueId(), System.currentTimeMillis());
        target.sendMessage("§6" + sender.getName() + " invited you to a party! Type /party accept");
    }
    
    private void acceptPartyInvite(Player player) {
        for (Party party : parties.values()) {
            if (party.pendingInvites.containsKey(player.getUniqueId())) {
                party.members.add(player.getUniqueId());
                playerParties.put(player.getUniqueId(), party);
                party.pendingInvites.remove(player.getUniqueId());
                
                player.sendMessage("§aYou joined the party!");
                broadcastToParty(party, "§6" + player.getName() + " joined the party!");
                return;
            }
        }
        
        player.sendMessage("§cNo pending party invites!");
    }
    
    private void leaveParty(Player player) {
        Party party = playerParties.remove(player.getUniqueId());
        if (party == null) {
            player.sendMessage("§cYou're not in a party!");
            return;
        }
        
        party.members.remove(player.getUniqueId());
        player.sendMessage("§aYou left the party!");
        broadcastToParty(party, "§6" + player.getName() + " left the party!");
    }
    
    private void listPartyMembers(Player player) {
        Party party = playerParties.get(player.getUniqueId());
        if (party == null) {
            player.sendMessage("§cYou're not in a party!");
            return;
        }
        
        player.sendMessage("§a=== Party Members ===");
        for (UUID uuid : party.members) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null) {
                player.sendMessage("  §f" + member.getName() + (uuid.equals(party.leader) ? " §6(Leader)" : ""));
            }
        }
    }
    
    private void showPartyHelp(Player player) {
        player.sendMessage("§a=== Party Commands ===");
        player.sendMessage("/party create - Create a party");
        player.sendMessage("/party invite <player> - Invite player");
        player.sendMessage("/party accept - Accept invite");
        player.sendMessage("/party leave - Leave party");
        player.sendMessage("/party list - List members");
    }
    
    private void broadcastToParty(Party party, String message) {
        for (UUID uuid : party.members) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null) {
                member.sendMessage(message);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        leaveParty(event.getPlayer());
    }
    
    public static PartySystemPlugin getInstance() {
        return instance;
    }
    
    private static class Party {
        UUID id;
        UUID leader;
        Set<UUID> members = new HashSet<>();
        Map<UUID, Long> pendingInvites = new HashMap<>();
        
        Party(UUID id, UUID leader) {
            this.id = id;
            this.leader = leader;
            this.members.add(leader);
        }
    }
}
