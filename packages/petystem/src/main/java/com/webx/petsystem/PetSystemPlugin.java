package com.webx.petsystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetSystemPlugin extends JavaPlugin implements Listener {
    private static PetSystemPlugin instance;
    private Map<UUID, Pet> playerPets = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        getCommand("pet").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                player.sendMessage("§cUsage: /pet [summon/dismiss/rename <name>]");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("summon")) {
                summonPet(player);
            } else if (args[0].equalsIgnoreCase("dismiss")) {
                dismissPet(player);
            } else if (args[0].equalsIgnoreCase("rename") && args.length > 1) {
                renamePet(player, args[1]);
            }
            
            return true;
        });
        
        getLogger().info("Pet System Plugin enabled!");
    }
    
    private void summonPet(Player player) {
        if (playerPets.containsKey(player.getUniqueId())) {
            player.sendMessage("§cYou already have a pet!");
            return;
        }
        
        ArmorStand pet = (ArmorStand) player.getWorld().spawnEntity(
            player.getLocation().add(1, 0, 0), 
            EntityType.ARMOR_STAND
        );
        
        pet.setCustomName("§6" + player.getName() + "'s Pet");
        pet.setCustomNameVisible(true);
        pet.setVisible(false);
        
        playerPets.put(player.getUniqueId(), new Pet(pet, "Pet"));
        player.sendMessage("§aYour pet has been summoned!");
    }
    
    private void dismissPet(Player player) {
        Pet pet = playerPets.remove(player.getUniqueId());
        if (pet == null) {
            player.sendMessage("§cYou don't have a pet!");
            return;
        }
        
        pet.entity.remove();
        player.sendMessage("§aYour pet has been dismissed!");
    }
    
    private void renamePet(Player player, String name) {
        Pet pet = playerPets.get(player.getUniqueId());
        if (pet == null) {
            player.sendMessage("§cYou don't have a pet!");
            return;
        }
        
        pet.name = name;
        pet.entity.setCustomName("§6" + name);
        player.sendMessage("§aYour pet has been renamed to: §f" + name);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Pet pet = playerPets.remove(event.getPlayer().getUniqueId());
        if (pet != null) {
            pet.entity.remove();
        }
    }
    
    public static PetSystemPlugin getInstance() {
        return instance;
    }
    
    private static class Pet {
        ArmorStand entity;
        String name;
        
        Pet(ArmorStand entity, String name) {
            this.entity = entity;
            this.name = name;
        }
    }
}
