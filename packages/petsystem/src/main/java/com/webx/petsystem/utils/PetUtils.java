package com.webx.petsystem.utils;

import com.webx.petsystem.models.Pet;
import java.util.UUID;

public class PetUtils {
    
    public static String getPetTypeEmoji(String type) {
        return switch(type) {
            case "DOG" -> "🐕";
            case "CAT" -> "🐈";
            case "RABBIT" -> "🐰";
            default -> "🐾";
        };
    }
    
    public static int calculatePetPower(Pet pet) {
        return pet.getLevel() * 10;
    }
}
