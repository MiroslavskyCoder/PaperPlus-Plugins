package com.webx.petsystem.managers;

import com.webx.petsystem.models.Pet;
import org.bukkit.entity.Player;
import java.util.*;

public class PetManager {
    private final Map<UUID, List<Pet>> playerPets = new HashMap<>();
    
    public void addPet(Player player, Pet pet) {
        playerPets.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(pet);
    }
    
    public List<Pet> getPets(Player player) {
        return playerPets.getOrDefault(player.getUniqueId(), Collections.emptyList());
    }
    
    public void removePet(Player player, Pet pet) {
        List<Pet> pets = playerPets.get(player.getUniqueId());
        if (pets != null) {
            pets.remove(pet);
        }
    }
}
