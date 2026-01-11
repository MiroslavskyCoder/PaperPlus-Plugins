package com.webx.simpleheal;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HealCommand implements CommandExecutor {
    
    private final SimpleHealPlugin plugin;
    
    public HealCommand(SimpleHealPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Heal self
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can heal themselves!");
                return true;
            }
            
            Player player = (Player) sender;
            
            if (!player.hasPermission("simpleheal.use")) {
                player.sendMessage(plugin.getMessage("no-permission"));
                return true;
            }
            
            // Check cooldown
            if (plugin.getCooldown() > 0 && !player.hasPermission("simpleheal.bypass")) {
                UUID uuid = player.getUniqueId();
                if (plugin.getCooldowns().containsKey(uuid)) {
                    long timeLeft = (plugin.getCooldowns().get(uuid) - System.currentTimeMillis()) / 1000;
                    if (timeLeft > 0) {
                        player.sendMessage("§cCooldown: " + timeLeft + " seconds");
                        return true;
                    }
                }
                plugin.getCooldowns().put(uuid, System.currentTimeMillis() + (plugin.getCooldown() * 1000L));
            }
            
            healPlayer(player);
            player.sendMessage(plugin.getMessage("healed-self"));
            
        } else {
            // Heal other player
            if (!sender.hasPermission("simpleheal.others")) {
                sender.sendMessage(plugin.getMessage("no-permission"));
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.getMessage("player-not-found"));
                return true;
            }
            
            healPlayer(target);
            sender.sendMessage(plugin.getMessage("healed-other").replace("%player%", target.getName()));
            
            if (sender instanceof Player) {
                target.sendMessage(plugin.getMessage("healed-by").replace("%healer%", sender.getName()));
            }
        }
        
        return true;
    }
    
    private void healPlayer(Player player) {
        if (plugin.isHealHealth()) {
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }
        if (plugin.isHealFood()) {
            player.setFoodLevel(20);
            player.setSaturation(20);
        }
        if (plugin.isClearEffects()) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        }
        if (plugin.isClearFire()) {
            player.setFireTicks(0);
        }
    }
}
