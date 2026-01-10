package com.webx.guildsadvanced;

import org.bukkit.plugin.java.JavaPlugin;

public class GuildsAdvancedPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("GuildsAdvanced plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GuildsAdvanced plugin disabled!");
    }
}
