package com.webx.skills;

import org.bukkit.plugin.java.JavaPlugin;

public class SkillsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Skills plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Skills plugin disabled!");
    }
}
