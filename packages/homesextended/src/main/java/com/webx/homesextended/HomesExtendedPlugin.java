package com.webx.homesextended;
import com.webx.homesextended.managers.HomeManager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class HomesExtendedPlugin extends JavaPlugin {
    private static HomesExtendedPlugin instance;
    private Map<UUID, List<Home>> playerHomes = new HashMap<>();
    private int maxHomes;
    private HomeManager homeManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
            homeManager = new HomeManager();
        maxHomes = getConfig().getInt("max-homes", 10);
        
        getCommand("home").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                player.sendMessage("§cUsage: /home [name]");
                return true;
            }
            
            goHome(player, args[0]);
            return true;
        });
        
        getCommand("sethome").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                player.sendMessage("§cUsage: /sethome <name>");
                return true;
            }
            
            setHome(player, args[0]);
            return true;
        });
        
        getLogger().info("Homes Extended Plugin enabled!");
    }
    
    private void setHome(Player player, String name) {
        List<Home> homes = playerHomes.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
        
        if (homes.size() >= maxHomes) {
            player.sendMessage("§cYou've reached maximum homes!");
            return;
        }
        
        homes.add(new Home(name, player.getLocation()));
        player.sendMessage("§aHome saved: " + name);
    }
    
    private void goHome(Player player, String name) {
        List<Home> homes = playerHomes.get(player.getUniqueId());
        if (homes == null) {
            player.sendMessage("§cNo homes set!");
            return;
        }
        
        for (Home home : homes) {
            if (home.name.equalsIgnoreCase(name)) {
                player.teleport(home.location);
                player.sendMessage("§aTeleported to home: " + name);
                return;
            }
        }
        
        player.sendMessage("§cHome not found!");
    }
    
    public static HomesExtendedPlugin getInstance() {
        return instance;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }
    
    private static class Home {
        String name;
        org.bukkit.Location location;
        
        Home(String name, org.bukkit.Location location) {
            this.name = name;
            this.location = location;
        }
    }
}
