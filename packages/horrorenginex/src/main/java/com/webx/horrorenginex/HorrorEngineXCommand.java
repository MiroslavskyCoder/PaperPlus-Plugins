package com.webx.horrorenginex;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command handler for HorrorEngineX
 */
public class HorrorEngineXCommand implements CommandExecutor {
    
    private final HorrorEngineXPlugin plugin;
    
    public HorrorEngineXCommand(HorrorEngineXPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return showHelp(sender);
        }
        
        String subcommand = args[0].toLowerCase();
        
        switch (subcommand) {
            case "help":
                return showHelp(sender);
                
            case "status":
                return showStatus(sender);
                
            case "event":
                return handleEventCommand(sender, args);
                
            case "effects":
                return handleEffectsCommand(sender, args);
                
            case "cinematic":
                return handleCinematicCommand(sender, args);
                
            case "glitch":
                return handleGlitchCommand(sender);
                
            case "bypass":
                return handleBypassCommand(sender);
                
            case "reload":
                return handleReload(sender);
                
            default:
                sender.sendMessage("§c✗ Unknown subcommand: " + subcommand);
                return false;
        }
    }
    
    private boolean showHelp(CommandSender sender) {
        sender.sendMessage("§6§l═══ HorrorEngineX Commands ═══");
        sender.sendMessage("§e/horrorenginex help §7- Show this help message");
        sender.sendMessage("§e/horrorenginex status §7- Show current status");
        sender.sendMessage("§e/horrorenginex event <start|stop> §7- Control horror events");
        sender.sendMessage("§e/horrorenginex effects <trigger|list> §7- Manage effects");
        sender.sendMessage("§e/horrorenginex cinematic §7- Apply cinematic effect");
        sender.sendMessage("§e/horrorenginex glitch §7- Trigger glitch effect");
        sender.sendMessage("§e/horrorenginex bypass §7- Toggle horror effects");
        sender.sendMessage("§e/horrorenginex reload §7- Reload configuration");
        return true;
    }
    
    private boolean showStatus(CommandSender sender) {
        HorrorConfigManager config = plugin.getConfigManager();
        
        sender.sendMessage("§6§l═══ HorrorEngineX Status ═══");
        sender.sendMessage("§eHorror Events: " + (config.isHorrorEventsEnabled() ? "§a✓ ON" : "§c✗ OFF"));
        sender.sendMessage("§eSound Effects: " + (config.isSoundEffectsEnabled() ? "§a✓ ON" : "§c✗ OFF"));
        sender.sendMessage("§eAtmospheric Effects: " + (config.isAtmosphericEffectsEnabled() ? "§a✓ ON" : "§c✗ OFF"));
        sender.sendMessage("§eJoin Messages: " + (config.isJoinMessageEnabled() ? "§a✓ ON" : "§c✗ OFF"));
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            sender.sendMessage("§eYour Bypass: " + (plugin.isBypassed(player) ? "§c✓ ON" : "§a✗ OFF"));
        }
        
        return true;
    }
    
    private boolean handleEventCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c✗ Usage: /horrorenginex event <start|stop>");
            return false;
        }
        
        String action = args[1].toLowerCase();
        HorrorEventManager eventManager = plugin.getEventManager();
        
        if ("start".equals(action)) {
            eventManager.startEventScheduler();
            sender.sendMessage("§a✓ Horror events started!");
            return true;
        } else if ("stop".equals(action)) {
            eventManager.stopEventScheduler();
            sender.sendMessage("§a✓ Horror events stopped!");
            return true;
        } else {
            sender.sendMessage("§c✗ Unknown action: " + action);
            return false;
        }
    }
    
    private boolean handleEffectsCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c✗ Usage: /horrorenginex effects <trigger|list>");
            return false;
        }
        
        String action = args[1].toLowerCase();
        
        if ("trigger".equals(action)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c✗ This command requires a player!");
                return false;
            }
            Player player = (Player) sender;
            plugin.getEffectsManager().triggerRandomHorrorEffect(player);
            sender.sendMessage("§a✓ Random horror effect triggered!");
            return true;
        } else if ("list".equals(action)) {
            sender.sendMessage("§6§l═══ Available Effects ═══");
            sender.sendMessage("§e• Blindness - Temporary darkness");
            sender.sendMessage("§e• Slowness - Movement slowdown");
            sender.sendMessage("§e• Nausea - Screen rotation");
            sender.sendMessage("§e• Weakness - Reduced damage");
            sender.sendMessage("§e• Mining Fatigue - Slower mining");
            sender.sendMessage("§e• Darkness - Ambient darkness");
            return true;
        } else {
            sender.sendMessage("§c✗ Unknown action: " + action);
            return false;
        }
    }
    
    private boolean handleBypassCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c✗ This command requires a player!");
            return false;
        }
        
        plugin.toggleBypass((Player) sender);
        return true;
    }
    
    private boolean handleCinematicCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c✗ This command requires a player!");
            return false;
        }
        
        Player player = (Player) sender;
        plugin.getCinematicManager().applyCinematicEffect(player);
        sender.sendMessage("§a✓ Cinematic horror effect applied!");
        return true;
    }
    
    private boolean handleGlitchCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c✗ This command requires a player!");
            return false;
        }
        
        Player player = (Player) sender;
        plugin.getCinematicManager().triggerRandomGlitch(player);
        sender.sendMessage("§a✓ Glitch effect triggered!");
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        try {
            plugin.reloadConfig();
            plugin.getConfigManager().loadConfig();
            sender.sendMessage("§a✓ Configuration reloaded!");
            return true;
        } catch (Exception e) {
            sender.sendMessage("§c✗ Failed to reload configuration: " + e.getMessage());
            return false;
        }
    }
}
