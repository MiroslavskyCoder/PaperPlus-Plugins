package com.webx.quests.managers;

import com.webx.quests.QuestsPlugin;
import org.bukkit.entity.Player;

import java.util.Map;

public class RewardManager {
    private final QuestsPlugin plugin;

    public RewardManager(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public void grantRewards(Player player, Map<String, Object> rewards) {
        for (Map.Entry<String, Object> reward : rewards.entrySet()) {
            switch (reward.getKey()) {
                case "money" -> {
                    double amount = ((Number) reward.getValue()).doubleValue();
                    // TODO: Give money via economy plugin
                }
                case "exp" -> {
                    int exp = ((Number) reward.getValue()).intValue();
                    player.giveExp(exp);
                }
                case "item" -> {
                    // TODO: Give item to player
                }
            }
        }
    }

    public void sendRewardMessage(Player player, Map<String, Object> rewards) {
        StringBuilder message = new StringBuilder("§aRewards: ");
        for (Map.Entry<String, Object> reward : rewards.entrySet()) {
            message.append("§6").append(reward.getKey()).append(": ").append(reward.getValue()).append(" ");
        }
        player.sendMessage(message.toString());
    }
}
