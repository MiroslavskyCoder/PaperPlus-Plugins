package com.webx.create2.commands;

import com.webx.create2.Create2Plugin;
import com.webx.create2.kinematic.KinematicNetwork;
import com.webx.create2.kinematic.KinematicNetworkManager;
import com.webx.create2.kinematic.KinematicNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main command handler for Create2
 * Inspired by Create's commands
 */
public class Create2Command implements CommandExecutor, TabCompleter {
    
    private final Create2Plugin plugin;
    
    public Create2Command(Create2Plugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subcommand = args[0].toLowerCase();
        
        switch (subcommand) {
            case "stats":
            case "statistics":
                return handleStats(sender);
                
            case "networks":
            case "net":
                return handleNetworks(sender);
                
            case "inspect":
            case "check":
                return handleInspect(sender);
                
            case "debug":
                return handleDebug(sender, args);
                
            case "reload":
                return handleReload(sender);
                
            case "help":
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private boolean handleStats(CommandSender sender) {
        KinematicNetworkManager manager = plugin.getNetworkManager();
        KinematicNetworkManager.NetworkStats stats = manager.getStats();
        
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(ChatColor.YELLOW + "  Create2 Statistics");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(ChatColor.GREEN + "Networks: " + ChatColor.WHITE + stats.totalNetworks);
        sender.sendMessage(ChatColor.GREEN + "Components: " + ChatColor.WHITE + stats.totalComponents);
        sender.sendMessage(ChatColor.GREEN + "Avg Size: " + ChatColor.WHITE + String.format("%.1f", stats.avgNetworkSize));
        sender.sendMessage(ChatColor.GREEN + "Largest: " + ChatColor.WHITE + stats.largestNetwork);
        sender.sendMessage(ChatColor.GREEN + "Overstressed: " + ChatColor.WHITE + 
            (stats.overstressedNetworks > 0 ? ChatColor.RED : ChatColor.GREEN) + stats.overstressedNetworks);
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        
        return true;
    }
    
    private boolean handleNetworks(CommandSender sender) {
        KinematicNetworkManager manager = plugin.getNetworkManager();
        List<KinematicNetwork> networks = new ArrayList<>(manager.getAllNetworks());
        
        if (networks.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No kinematic networks found");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(ChatColor.YELLOW + "  Active Networks (" + networks.size() + ")");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        
        // Sort by size
        networks.sort((a, b) -> Integer.compare(b.getSize(), a.getSize()));
        
        int limit = Math.min(10, networks.size());
        for (int i = 0; i < limit; i++) {
            KinematicNetwork network = networks.get(i);
            
            String stressColor = network.isOverstressed() ? ChatColor.RED.toString() : ChatColor.GREEN.toString();
            String stressBar = createStressBar(network.getStressPercentage());
            
            sender.sendMessage(String.format("%s#%d %s[%d blocks] %sRPM: %.1f %sStress: %s%s",
                ChatColor.AQUA,
                i + 1,
                ChatColor.GRAY,
                network.getSize(),
                ChatColor.YELLOW,
                network.getRpm(),
                ChatColor.WHITE,
                stressBar,
                stressColor + String.format(" %.0f%%", network.getStressPercentage())
            ));
        }
        
        if (networks.size() > 10) {
            sender.sendMessage(ChatColor.GRAY + "... and " + (networks.size() - 10) + " more");
        }
        
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        
        return true;
    }
    
    private boolean handleInspect(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }
        
        Player player = (Player) sender;
        Vector3i pos = new Vector3i(
            player.getLocation().getBlockX(),
            player.getLocation().getBlockY() - 1,
            player.getLocation().getBlockZ()
        );
        
        KinematicNetworkManager manager = plugin.getNetworkManager();
        KinematicNode node = manager.getNode(pos);
        
        if (node == null) {
            sender.sendMessage(ChatColor.RED + "No kinematic component at your feet");
            return true;
        }
        
        KinematicNetwork network = manager.getNetwork(pos);
        
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(ChatColor.YELLOW + "  Component Inspector");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(ChatColor.GREEN + "Type: " + ChatColor.WHITE + node.getType().name());
        sender.sendMessage(ChatColor.GREEN + "Position: " + ChatColor.WHITE + formatVector(pos));
        sender.sendMessage(ChatColor.GREEN + "Axis: " + ChatColor.WHITE + node.getAxis().name());
        sender.sendMessage(ChatColor.GREEN + "RPM: " + ChatColor.WHITE + String.format("%.1f", node.getRpm()));
        sender.sendMessage(ChatColor.GREEN + "Stress Impact: " + ChatColor.WHITE + String.format("%.1f SU", node.getStressImpact()));
        sender.sendMessage(ChatColor.GREEN + "Stress Capacity: " + ChatColor.WHITE + String.format("%.1f SU", node.getStressCapacity()));
        
        if (network != null) {
            sender.sendMessage(ChatColor.GOLD + "───────────────────────────────");
            sender.sendMessage(ChatColor.YELLOW + "Network Info:");
            sender.sendMessage(ChatColor.GREEN + "Size: " + ChatColor.WHITE + network.getSize() + " blocks");
            sender.sendMessage(ChatColor.GREEN + "Network RPM: " + ChatColor.WHITE + String.format("%.1f", network.getRpm()));
            sender.sendMessage(ChatColor.GREEN + "Total Stress: " + ChatColor.WHITE + 
                String.format("%.1f / %.1f SU", network.getStress(), network.getStressCapacity()));
            
            String status = network.isOverstressed() ? ChatColor.RED + "OVERSTRESSED" : ChatColor.GREEN + "OK";
            sender.sendMessage(ChatColor.GREEN + "Status: " + status);
        }
        
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        
        return true;
    }
    
    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("create2.debug")) {
            sender.sendMessage(ChatColor.RED + "No permission");
            return true;
        }
        
        if (args.length < 2) {
            boolean current = plugin.isDebugEnabled();
            sender.sendMessage(ChatColor.YELLOW + "Debug mode: " + (current ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
            sender.sendMessage(ChatColor.GRAY + "Usage: /create2 debug <on|off>");
            return true;
        }
        
        boolean enable = args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true");
        plugin.getConfig().set("debug.show-networks", enable);
        plugin.saveConfig();
        
        sender.sendMessage(ChatColor.GREEN + "Debug mode " + (enable ? "enabled" : "disabled"));
        
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("create2.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission");
            return true;
        }
        
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded");
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(ChatColor.YELLOW + "  Create2 Commands");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(ChatColor.AQUA + "/create2 stats" + ChatColor.GRAY + " - View statistics");
        sender.sendMessage(ChatColor.AQUA + "/create2 networks" + ChatColor.GRAY + " - List active networks");
        sender.sendMessage(ChatColor.AQUA + "/create2 inspect" + ChatColor.GRAY + " - Inspect component at your feet");
        sender.sendMessage(ChatColor.AQUA + "/create2 debug <on|off>" + ChatColor.GRAY + " - Toggle debug mode");
        sender.sendMessage(ChatColor.AQUA + "/create2 reload" + ChatColor.GRAY + " - Reload config");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
    }
    
    private String createStressBar(double percentage) {
        int bars = 10;
        int filled = (int) (percentage / 10);
        filled = Math.min(bars, Math.max(0, filled));
        
        StringBuilder bar = new StringBuilder(ChatColor.DARK_GRAY + "[");
        
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                if (percentage < 50) {
                    bar.append(ChatColor.GREEN);
                } else if (percentage < 80) {
                    bar.append(ChatColor.YELLOW);
                } else {
                    bar.append(ChatColor.RED);
                }
                bar.append("█");
            } else {
                bar.append(ChatColor.DARK_GRAY + "█");
            }
        }
        
        bar.append(ChatColor.DARK_GRAY).append("]");
        return bar.toString();
    }
    
    private String formatVector(Vector3i vec) {
        return String.format("(%d, %d, %d)", vec.x, vec.y, vec.z);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("stats", "networks", "inspect", "debug", "reload", "help");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return Arrays.asList("on", "off");
        }
        
        return new ArrayList<>();
    }
}
