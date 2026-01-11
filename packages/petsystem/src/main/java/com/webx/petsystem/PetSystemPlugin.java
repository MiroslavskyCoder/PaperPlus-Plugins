package com.webx.petsystem;

import com.webx.petsystem.commands.PetCommand;
import com.webx.petsystem.commands.PetRenameCommand;
import com.webx.petsystem.listeners.PetDamageListener;
import com.webx.petsystem.listeners.PetJoinListener;
import com.webx.petsystem.managers.PetManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PetSystemPlugin extends JavaPlugin {
    private static PetSystemPlugin instance;
    private PetManager petManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize manager
        this.petManager = new PetManager();
        
        // Register commands
        getCommand("pet").setExecutor(new PetCommand(this));
        getCommand("petrename").setExecutor(new PetRenameCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PetJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PetDamageListener(this), this);
        
        getLogger().info("PetSystem Plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("PetSystem Plugin disabled!");
    }
    
    public static PetSystemPlugin getInstance() {
        return instance;
    }
    
    public PetManager getPetManager() {
        return petManager;
    }
}
