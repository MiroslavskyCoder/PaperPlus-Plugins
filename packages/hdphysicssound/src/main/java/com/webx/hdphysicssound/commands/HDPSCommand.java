package com.webx.hdphysicssound.commands;

import com.webx.hdphysicssound.HDPhysicsSoundPlugin;
import com.webx.hdphysicssound.config.PhysicsConfig;
import com.webx.hdphysicssound.engine.SoundPhysicsEngine;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HDPSCommand implements CommandExecutor, TabCompleter {

    private final HDPhysicsSoundPlugin plugin;
    private final SoundPhysicsEngine engine;

    public HDPSCommand(HDPhysicsSoundPlugin plugin, SoundPhysicsEngine engine) {
        this.plugin = plugin;
        this.engine = engine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "reload":
                return handleReload(sender);
            case "debug":
                return handleDebug(sender, args);
            case "test":
                return handleTest(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("hdps.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        plugin.reloadPhysicsConfig();
        sender.sendMessage(ChatColor.GREEN + "HDPhysicsSound config reloaded.");
        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("hdps.debug")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        PhysicsConfig cfg = plugin.getPhysicsConfig();
        sender.sendMessage(ChatColor.GOLD + "Physics Debug");
        sender.sendMessage(ChatColor.GRAY + "Max Distance: " + cfg.getMaxDistance());
        sender.sendMessage(ChatColor.GRAY + "Air Absorption: " + cfg.getAirAbsorptionPerMeter());
        sender.sendMessage(ChatColor.GRAY + "Occlusion Steps: " + cfg.getOcclusionSteps());
        sender.sendMessage(ChatColor.GRAY + "Occlusion Penalty: " + cfg.getOcclusionPenalty());
        sender.sendMessage(ChatColor.GRAY + "Reverb: " + (cfg.isReverbEnabled() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        return true;
    }

    private boolean handleTest(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Players only.");
            return true;
        }
        Player player = (Player) sender;
        Location loc = player.getLocation();

        float volume = 1.0f;
        float pitch = 1.0f;
        if (args.length >= 2) {
            try {
                volume = Float.parseFloat(args[1]);
            } catch (NumberFormatException ignored) { }
        }
        if (args.length >= 3) {
            try {
                pitch = Float.parseFloat(args[2]);
            } catch (NumberFormatException ignored) { }
        }

        engine.broadcastPhysicalSound(loc, Sound.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, volume, pitch);
        sender.sendMessage(ChatColor.GREEN + "Played test sound with physics.");
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "HDPhysicsSound");
        sender.sendMessage(ChatColor.AQUA + "/hdps test [vol] [pitch]" + ChatColor.GRAY + " - play test sound");
        sender.sendMessage(ChatColor.AQUA + "/hdps reload" + ChatColor.GRAY + " - reload config");
        sender.sendMessage(ChatColor.AQUA + "/hdps debug" + ChatColor.GRAY + " - show physics values");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("test", "reload", "debug");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("test")) {
            return Arrays.asList("1.0", "0.5", "2.0");
        }
        return new ArrayList<>();
    }
}
