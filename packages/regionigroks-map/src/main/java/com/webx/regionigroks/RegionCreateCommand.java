package com.webx.regionigroks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionCreateCommand implements CommandExecutor {
    private final RegionigroksMapPlugin plugin;

    public RegionCreateCommand(RegionigroksMapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        Player player = (Player) sender;

        String sub = args.length > 0 ? args[0].toLowerCase() : "create";
        switch (sub) {
            case "create":
                CreateRegionGui.openColorSelector(player);
                PendingRegion pr = new PendingRegion();
                pr.setCenter(player.getLocation());
                plugin.getPendingRegions().put(player.getUniqueId(), pr);
                return true;
            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /region join <name>");
                    return true;
                }
                String nameJoin = args[1];
                plugin.getRegionManager().getRegionByName(nameJoin).ifPresentOrElse(r -> {
                    if (plugin.getRegionManager().joinRegion(r, player.getUniqueId())) {
                        player.sendMessage(ChatColor.GREEN + "Joined region '" + r.getName() + "'.");
                        plugin.saveRegions();
                    } else {
                        player.sendMessage(ChatColor.RED + "Cannot join. Region is closed or you are not invited.");
                    }
                }, () -> player.sendMessage(ChatColor.RED + "Region not found."));
                return true;
            case "leave":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /region leave <name>");
                    return true;
                }
                String nameLeave = args[1];
                plugin.getRegionManager().getRegionByName(nameLeave).ifPresentOrElse(r -> {
                    if (plugin.getRegionManager().leaveRegion(r, player.getUniqueId())) {
                        player.sendMessage(ChatColor.GREEN + "Left region '" + r.getName() + "'.");
                        plugin.saveRegions();
                    } else {
                        player.sendMessage(ChatColor.RED + "Owner cannot leave their own region.");
                    }
                }, () -> player.sendMessage(ChatColor.RED + "Region not found."));
                return true;
            case "invite":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /region invite <player> <name>");
                    return true;
                }
                String targetName = args[1];
                String regionName = args[2];
                Player target = player.getServer().getPlayerExact(targetName);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
                plugin.getRegionManager().getRegionByName(regionName).ifPresentOrElse(r -> {
                    if (!r.getOwner().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "Only the owner can invite to this region.");
                        return;
                    }
                    plugin.getRegionManager().invite(r, target.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "Invited '" + target.getName() + "' to region '" + r.getName() + "'.");
                    target.sendMessage(ChatColor.YELLOW + "You have been invited to region '" + r.getName() + "'. Use /region join " + r.getName());
                    plugin.saveRegions();
                }, () -> player.sendMessage(ChatColor.RED + "Region not found."));
                return true;
            case "setprivacy":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /region setprivacy <open|closed> <name>");
                    return true;
                }
                String privacyStr = args[1].toLowerCase();
                String namePriv = args[2];
                Region.Privacy pv = privacyStr.equals("closed") ? Region.Privacy.CLOSED : Region.Privacy.OPEN;
                plugin.getRegionManager().getRegionByName(namePriv).ifPresentOrElse(r -> {
                    if (!r.getOwner().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "Only the owner can change privacy.");
                        return;
                    }
                    plugin.getRegionManager().setPrivacy(r, pv);
                    player.sendMessage(ChatColor.GREEN + "Privacy set to " + pv + " for region '" + r.getName() + "'.");
                    plugin.saveRegions();
                }, () -> player.sendMessage(ChatColor.RED + "Region not found."));
                return true;
            default:
                player.sendMessage(ChatColor.YELLOW + "Usage: /region create | join <name> | leave <name> | invite <player> <name> | setprivacy <open|closed> <name>");
                return true;
        }
    }
}
