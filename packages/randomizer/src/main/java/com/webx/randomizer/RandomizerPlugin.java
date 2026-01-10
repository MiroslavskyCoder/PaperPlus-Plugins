package com.webx.randomizer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class RandomizerPlugin extends JavaPlugin implements Listener {
    private static RandomizerPlugin instance;
    private Random random = new Random();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        
        getCommand("randomizer").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            Material randomMaterial = getRandomMaterial();
            ItemStack item = new ItemStack(randomMaterial);
            player.getInventory().addItem(item);
            
            player.sendMessage("§aYou received random item: §f" + randomMaterial.name());
            
            return true;
        });
        
        getLogger().info("Randomizer Plugin enabled!");
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (getConfig().getBoolean("random-drops", false)) {
            if (random.nextDouble() < 0.1) { // 10% шанс
                Material randomDrop = getRandomMaterial();
                event.setExpToDrop(event.getExpToDrop() * 2);
                event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation(),
                    new ItemStack(randomDrop)
                );
            }
        }
    }
    
    private Material getRandomMaterial() {
        Material[] materials = {
            Material.DIAMOND,
            Material.GOLD_INGOT,
            Material.EMERALD,
            Material.IRON_INGOT,
            Material.LAPIS_LAZULI
        };
        return materials[random.nextInt(materials.length)];
    }
    
    public static RandomizerPlugin getInstance() {
        return instance;
    }
}
