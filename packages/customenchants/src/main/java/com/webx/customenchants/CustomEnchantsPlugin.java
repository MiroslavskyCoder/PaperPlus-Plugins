package com.webx.customenchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomEnchantsPlugin extends JavaPlugin implements Listener {
    private static CustomEnchantsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Custom Enchants Plugin enabled!");
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        
        Player attacker = (Player) event.getDamager();
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        
        // Проверка на пользовательские зачарования
        if (weapon != null) {
            int vamp = getCustomEnchantLevel(weapon, "Vampire");
            int sharpness = weapon.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            
            if (vamp > 0) {
                double damage = event.getDamage();
                attacker.setHealth(Math.min(20, attacker.getHealth() + damage * vamp * 0.2));
            }
            
            if (sharpness > 0) {
                event.setDamage(event.getDamage() * (1 + sharpness * 0.2));
            }
        }
    }
    
    private int getCustomEnchantLevel(ItemStack item, String enchantName) {
        // Будет реализовано с Vault/NBT
        return 0;
    }
    
    public static CustomEnchantsPlugin getInstance() {
        return instance;
    }
}
