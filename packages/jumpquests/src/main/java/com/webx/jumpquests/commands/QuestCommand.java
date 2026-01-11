package com.webx.jumpquests.commands;

import com.webx.jumpquests.JumpQuestsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class QuestCommand implements CommandExecutor {
    private final JumpQuestsPlugin plugin;
    
    public QuestCommand(JumpQuestsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6=== Jump Quests ===");
        sender.sendMessage("§f" + plugin.getQuestManager().getAllQuests().size() + " quests available");
        
        return true;
    }
}
