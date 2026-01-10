package com.webx.events;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EventsCommand implements CommandExecutor {
    private EventsManager manager;
    
    public EventsCommand(EventsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§6Active Events:");
        manager.getActiveEvents().forEach(event -> 
            sender.sendMessage("§b" + event.name + "§7 - §f" + event.description)
        );
        return true;
    }
}
