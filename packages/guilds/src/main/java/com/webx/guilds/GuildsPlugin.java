package com.webx.guilds;
import com.webx.guilds.managers.GuildManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GuildsPlugin extends JavaPlugin {
    private static GuildsPlugin instance;
    private Map<UUID, Guild> playerGuilds = new HashMap<>();
    private Map<String, Guild> guilds = new HashMap<>();
    private GuildManager guildManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
            guildManager = new GuildManager();
        
        getCommand("guild").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                showGuildHelp(player);
                return true;
            }
            
            switch (args[0].toLowerCase()) {
                case "create":
                    if (args.length > 1) createGuild(player, args[1]);
                    break;
                case "invite":
                    if (args.length > 1) inviteToGuild(player, args[1]);
                    break;
                case "leave":
                    leaveGuild(player);
                    break;
                case "info":
                    showGuildInfo(player);
                    break;
            }
            
            return true;
        });
        
        getLogger().info("Guilds Plugin enabled!");
    }
    
    private void createGuild(Player player, String guildName) {
        if (playerGuilds.containsKey(player.getUniqueId())) {
            player.sendMessage("§cYou are already in a guild!");
            return;
        }
        
        Guild guild = new Guild(guildName, player.getUniqueId());
        guilds.put(guildName, guild);
        playerGuilds.put(player.getUniqueId(), guild);
        
        player.sendMessage("§aGuild created: " + guildName);
    }
    
    private void inviteToGuild(Player sender, String playerName) {
        Guild guild = playerGuilds.get(sender.getUniqueId());
        if (guild == null || !guild.leader.equals(sender.getUniqueId())) {
            sender.sendMessage("§cYou're not in a guild or aren't leader!");
            return;
        }
        
        Player target = Bukkit.getPlayer(playerName);
        if (target != null) {
            target.sendMessage("§6You've been invited to guild: " + guild.name);
        }
    }
    
    private void leaveGuild(Player player) {
        Guild guild = playerGuilds.remove(player.getUniqueId());
        if (guild != null) {
            guild.members.remove(player.getUniqueId());
            player.sendMessage("§aYou left the guild!");
        }
    }
    
    private void showGuildInfo(Player player) {
        Guild guild = playerGuilds.get(player.getUniqueId());
        if (guild == null) {
            player.sendMessage("§cYou're not in a guild!");
            return;
        }
        
        player.sendMessage("§a=== Guild Info ===");
        player.sendMessage("§6Name: §f" + guild.name);
        player.sendMessage("§6Members: §f" + guild.members.size());
        player.sendMessage("§6Level: §f" + guild.level);
    }
    
    private void showGuildHelp(Player player) {
        player.sendMessage("§a=== Guild Commands ===");
        player.sendMessage("/guild create <name> - Create guild");
        player.sendMessage("/guild invite <player> - Invite player");
        player.sendMessage("/guild leave - Leave guild");
        player.sendMessage("/guild info - Guild info");
    }
    
    public static GuildsPlugin getInstance() {
        return instance;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }
    
    private static class Guild {
        String name;
        UUID leader;
        Set<UUID> members = new HashSet<>();
        int level = 1;
        
        Guild(String name, UUID leader) {
            this.name = name;
            this.leader = leader;
            this.members.add(leader);
        }
    }
}
