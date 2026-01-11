package com.webx.simpleheal;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealAllCommand implements CommandExecutor {
    
    private final SimpleHealPlugin plugin;
    
    public HealAllCommand(SimpleHealPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simpleheal.all")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        int healed = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            healPlayer(player);
            healed++;
        }
        
        Bukkit.broadcastMessage(plugin.getMessage("healed-all"));
        sender.sendMessage("§aHealed " + healed + " players!");
        
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
