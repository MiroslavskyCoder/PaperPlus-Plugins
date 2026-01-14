package com.webx.hdphysicssound;

import com.webx.hdphysicssound.commands.HDPSCommand;
import com.webx.hdphysicssound.config.PhysicsConfig;
import com.webx.hdphysicssound.engine.SoundPhysicsEngine;
import com.webx.hdphysicssound.listeners.SoundEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HDPhysicsSoundPlugin extends JavaPlugin {

    private PhysicsConfig physicsConfig;
    private SoundPhysicsEngine physicsEngine;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPhysicsConfig();

        physicsEngine = new SoundPhysicsEngine(this, physicsConfig);

        Bukkit.getPluginManager().registerEvents(new SoundEventListener(this, physicsEngine), this);
        HDPSCommand command = new HDPSCommand(this, physicsEngine);
        getCommand("hdps").setExecutor(command);
        getCommand("hdps").setTabCompleter(command);

        getLogger().info("HDPhysicsSound enabled. Occlusion and reverb online.");
    }

    @Override
    public void onDisable() {
        getLogger().info("HDPhysicsSound disabled.");
    }

    public void reloadPhysicsConfig() {
        reloadConfig();
        physicsConfig = PhysicsConfig.fromConfig(getConfig());
        if (physicsEngine != null) {
            physicsEngine.setConfig(physicsConfig);
        }
    }

    public PhysicsConfig getPhysicsConfig() {
        return physicsConfig;
    }
}
