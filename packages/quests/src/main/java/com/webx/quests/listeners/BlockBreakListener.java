package com.webx.quests.listeners;

import com.webx.quests.QuestsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final QuestsPlugin plugin;

    public BlockBreakListener(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // TODO: Track block break objectives
    }
}
