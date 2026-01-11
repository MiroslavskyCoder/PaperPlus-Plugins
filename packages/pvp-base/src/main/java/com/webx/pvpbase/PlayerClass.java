package com.webx.pvpbase;

import org.bukkit.inventory.ItemStack;
import java.util.List;

public class PlayerClass {
    private final String name;
    private final String description;
    private final ItemStack icon;
    private final List<ItemStack> equipment;
    private final List<ItemStack> abilities;

    public PlayerClass(String name, String description, ItemStack icon, List<ItemStack> equipment, List<ItemStack> abilities) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.equipment = equipment;
        this.abilities = abilities;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemStack getIcon() { return icon; }
    public List<ItemStack> getEquipment() { return equipment; }
    public List<ItemStack> getAbilities() { return abilities; }
}
