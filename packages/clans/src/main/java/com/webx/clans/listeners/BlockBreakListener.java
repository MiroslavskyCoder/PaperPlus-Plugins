package com.webx.clans.listeners;

import com.webx.clans.ClansPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final ClansPlugin plugin;

    public BlockBreakListener(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String claimingClan = plugin.getTerritoryManager().getClaimingClan(event.getBlock().getChunk());
        if (claimingClan == null) return;

        var playerClan = plugin.getClanManager().getClanByMember(event.getPlayer().getUniqueId());
        if (playerClan == null || !playerClan.getName().equals(claimingClan)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou can't break blocks in enemy territory!");
        }
    }
}
