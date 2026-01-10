package com.webx.warps.commands;

import com.webx.warps.WarpsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final WarpsPlugin plugin;
    private final Map<String, CommandExecutor> commands;

    public CommandRegistry(WarpsPlugin plugin) {
        this.plugin = plugin;
        this.commands = new HashMap<>();
    }

    public void register(String name, CommandExecutor executor) {
        commands.put(name, executor);
        plugin.getCommand(name).setExecutor(executor);
    }

    public CommandExecutor getExecutor(String name) {
        return commands.get(name);
    }
}
