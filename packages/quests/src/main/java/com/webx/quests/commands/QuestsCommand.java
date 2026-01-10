package com.webx.quests.commands;

import com.webx.quests.QuestsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestsCommand implements CommandExecutor {
    private final QuestsPlugin plugin;

    public QuestsCommand(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6§lActive Quests:");
            plugin.getProgressManager().getActiveQuests(player.getUniqueId()).forEach(progress -> {
                player.sendMessage("§e  - §6" + progress.getQuestId());
            });
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "list" -> {
                player.sendMessage("§6§lAvailable Quests:");
                plugin.getQuestManager().getAllQuests().forEach(quest -> {
                    player.sendMessage("§e  - §6" + quest.getName() + " §7(Level " + quest.getLevel() + ")");
                });
            }
            case "accept" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /quests accept <quest>");
                    return true;
                }
                String questId = args[1];
                plugin.getProgressManager().startQuest(player.getUniqueId(), questId);
                player.sendMessage("§aQuest accepted!");
            }
            case "info" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /quests info <quest>");
                    return true;
                }
                var quest = plugin.getQuestManager().getQuest(args[1]);
                if (quest != null) {
                    player.sendMessage("§6§l" + quest.getName());
                    player.sendMessage("§7" + quest.getDescription());
                }
            }
            case "abandon" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /quests abandon <quest>");
                    return true;
                }
                plugin.getProgressManager().abandonQuest(player.getUniqueId(), args[1]);
                player.sendMessage("§aQuest abandoned!");
            }
            default -> {
                player.sendMessage("§cUsage: /quests <list|accept|info|abandon>");
            }
        }

        return true;
    }
}
