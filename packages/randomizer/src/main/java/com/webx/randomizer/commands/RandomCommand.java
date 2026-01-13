package com.webx.randomizer.commands;

import com.webx.randomizer.RandomizerPlugin;
import com.webx.randomizer.managers.RandomizerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for randomizer commands
 */
public class RandomCommand implements CommandExecutor {
    private final RandomizerPlugin plugin;
    private final RandomizerManager manager;
    
    public RandomCommand(RandomizerPlugin plugin, RandomizerManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }
        
        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();
        
        switch (cmd) {
            case "randomtp":
                return handleRandomTeleport(player);
                
            case "randomitem":
                return handleRandomItem(player);
                
            case "randomnumber":
                return handleRandomNumber(player);
                
            case "randomizer":
                return handleRandomizerMenu(player);
                
            default:
                return false;
        }
    }
    
    /**
     * Handle random teleport command
     */
    private boolean handleRandomTeleport(Player player) {
        player.sendMessage("§7Поиск безопасного места...");
        manager.randomTeleport(player);
        return true;
    }
    
    /**
     * Handle random item command
     */
    private boolean handleRandomItem(Player player) {
        manager.giveRandomItem(player);
        return true;
    }
    
    /**
     * Handle random number command
     */
    private boolean handleRandomNumber(Player player) {
        int number = manager.getRandomNumber();
        player.sendMessage("§aВаше случайное число: §f" + number);
        return true;
    }
    
    /**
     * Handle main randomizer menu
     */
    private boolean handleRandomizerMenu(Player player) {
        player.sendMessage("§6§l=== RANDOMIZER ===");
        player.sendMessage("§a/randomtp §7- Телепорт в случайное место");
        player.sendMessage("§a/randomitem §7- Получить случайный предмет (дерево/палки)");
        player.sendMessage("§a/randomnumber §7- Получить случайное число");
        
        if (!manager.checkCooldown(player)) {
            long timeLeft = manager.getRemainingCooldown(player) / 1000;
            player.sendMessage("§eКулдаун: §f" + timeLeft + " секунд");
        } else {
            player.sendMessage("§aКулдаун готов!");
        }
        
        return true;
    }
}
