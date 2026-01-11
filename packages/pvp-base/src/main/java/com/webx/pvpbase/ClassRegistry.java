package com.webx.pvpbase;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class ClassRegistry {
    private final Map<String, PlayerClass> classes = new HashMap<>();

    public ClassRegistry() {
        registerDefaultClasses();
    }

    private void registerDefaultClasses() {
        // Warrior class
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack armor = new ItemStack(Material.DIAMOND_CHESTPLATE);
        classes.put("Warrior", new PlayerClass(
            "Warrior",
            "High health, strong melee damage",
            new ItemStack(Material.DIAMOND_SWORD),
            Arrays.asList(armor, new ItemStack(Material.DIAMOND_LEGGINGS)),
            Collections.singletonList(sword)
        ));

        // Archer class
        ItemStack bow = new ItemStack(Material.BOW);
        classes.put("Archer", new PlayerClass(
            "Archer",
            "Range damage, high mobility",
            bow,
            Collections.singletonList(new ItemStack(Material.LEATHER_CHESTPLATE)),
            Arrays.asList(bow, new ItemStack(Material.ARROW, 32))
        ));

        // Mage class
        ItemStack staff = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = staff.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("Mage Staff");
            staff.setItemMeta(meta);
        }
        classes.put("Mage", new PlayerClass(
            "Mage",
            "Magic abilities, crowd control",
            staff,
            Collections.singletonList(new ItemStack(Material.PURPLE_WOOL)),
            Collections.singletonList(staff)
        ));

        // Tank class
        ItemStack shield = new ItemStack(Material.SHIELD);
        classes.put("Tank", new PlayerClass(
            "Tank",
            "Maximum defense, low mobility",
            shield,
            Arrays.asList(
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_BOOTS)
            ),
            Collections.singletonList(shield)
        ));
    }

    public PlayerClass getClass(String className) {
        return classes.get(className);
    }

    public Collection<PlayerClass> getAllClasses() {
        return classes.values();
    }

    public void registerClass(String key, PlayerClass playerClass) {
        classes.put(key, playerClass);
    }
}
