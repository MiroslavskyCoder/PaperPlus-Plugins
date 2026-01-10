package com.webx.miningevents;
import com.webx.miningevents.managers.MiningEventManager;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiningEventsPlugin extends JavaPlugin implements Listener {
    private static MiningEventsPlugin instance;
    private Map<UUID, Integer> playerOre = new HashMap<>();
    private MiningEventManager miningEventManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
            miningEventManager = new MiningEventManager();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Mining Events Plugin enabled!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isOre(event.getBlock().getType())) {
            UUID uuid = event.getPlayer().getUniqueId();
            int count = playerOre.getOrDefault(uuid, 0) + 1;
            playerOre.put(uuid, count);
            
            if (count % 10 == 0) {
                event.getPlayer().sendMessage("§6You've mined §f" + count + " §6ore!");
            }
            
            // Можно добавить награды за определенное количество руды
        }
    }
    
    private boolean isOre(Material material) {
        return material == Material.DIAMOND_ORE ||
               material == Material.GOLD_ORE ||
               material == Material.IRON_ORE ||
               material == Material.COAL_ORE ||
               material == Material.LAPIS_ORE;
    }
    
    public static MiningEventsPlugin getInstance() {
        return instance;
    
        public MiningEventManager getMiningEventManager() {
            return miningEventManager;
        }
    }
}
