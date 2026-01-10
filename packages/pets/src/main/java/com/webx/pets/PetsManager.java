package com.webx.pets;

import org.bukkit.entity.Player;
import java.util.*;

public class PetsManager {
    private Map<UUID, List<Pet>> playerPets = new HashMap<>();
    
    public void addPet(Player player, Pet pet) {
        UUID uuid = player.getUniqueId();
        playerPets.computeIfAbsent(uuid, k -> new ArrayList<>()).add(pet);
        player.sendMessage("§6✓ Added pet: " + pet.getName());
    }
    
    public List<Pet> getPets(Player player) {
        return playerPets.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }
    
    public void removePet(Player player, String petName) {
        playerPets.getOrDefault(player.getUniqueId(), new ArrayList<>())
            .removeIf(p -> p.getName().equals(petName));
    }
    
    static class Pet {
        private String name;
        private String type;
        private int level;
        
        public Pet(String name, String type) {
            this.name = name;
            this.type = type;
            this.level = 1;
        }
        
        public String getName() { return name; }
        public String getType() { return type; }
        public int getLevel() { return level; }
    }
}
