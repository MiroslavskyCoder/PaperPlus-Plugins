package com.webx.petsystem.api;

import com.webx.petsystem.models.Pet;
import java.util.List;
import java.util.UUID;

public class PetSystemAPI {
    
    public static List<Pet> getPlayerPets(UUID uuid) {
        // Returns player pets
        return List.of();
    }
    
    public static int getPetCount(UUID uuid) {
        // Returns pet count
        return 0;
    }
}
