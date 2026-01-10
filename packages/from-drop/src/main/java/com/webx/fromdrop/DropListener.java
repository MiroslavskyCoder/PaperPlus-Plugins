package com.webx.fromdrop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class DropListener implements Listener {
    private final DropConfig config;
    private final Random random = new Random();

    public DropListener(DropConfig config) {
        this.config = config;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Material broken = event.getBlock().getType();
        config.getBlockDrops(broken).ifPresent(entries -> {
            Player p = event.getPlayer();
            for (DropEntry entry : entries) {
                if (random.nextDouble() <= entry.getChance()) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(entry.getMaterial(), entry.getAmount()));
                    announce(p, entry);
                }
            }
        });
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        EntityType type = event.getEntityType();
        Material key = Material.matchMaterial(type.name());
        if (key == null) return;
        config.getMobDrops(key).ifPresent(entries -> {
            Player killer = event.getEntity().getKiller();
            for (DropEntry entry : entries) {
                if (random.nextDouble() <= entry.getChance()) {
                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(entry.getMaterial(), entry.getAmount()));
                    announce(killer, entry);
                }
            }
        });
    }

    private void announce(Player player, DropEntry entry) {
        if (!config.isAnnounce()) return;
        String msg = config.getMessage()
                .replace("%item%", entry.getMaterial().name())
                .replace("%amount%", String.valueOf(entry.getAmount()));
        player.sendMessage(ChatColor.YELLOW + msg);
    }
}
