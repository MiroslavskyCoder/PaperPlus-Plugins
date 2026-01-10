package com.webx.dance;

import org.bukkit.plugin.java.JavaPlugin;

public class DancePlugin extends JavaPlugin {
    private DanceManager danceManager;
    private MouthAnimator mouthAnimator;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        danceManager = new DanceManager(this);
        getCommand("dance").setExecutor(new DanceCommand(danceManager));
        mouthAnimator = new MouthAnimator(this);
        getServer().getPluginManager().registerEvents(new MouthListener(this, mouthAnimator), this);
        getLogger().info("Dance 0.1.0 enabled - 20+ unique dances loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Dance plugin disabled");
    }

    public DanceManager getDanceManager() {
        return danceManager;
    }

    public MouthAnimator getMouthAnimator(){
        return mouthAnimator;
    }
}
