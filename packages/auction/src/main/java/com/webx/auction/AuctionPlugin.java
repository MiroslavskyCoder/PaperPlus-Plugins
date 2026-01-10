package com.webx.auction;

import org.bukkit.plugin.java.JavaPlugin;

public class AuctionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Auction plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Auction plugin disabled!");
    }
}
