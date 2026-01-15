package com.webx.ranks.gui;

import org.bukkit.entity.Player;
import com.webx.ranks.models.Rank;
import com.webx.ranks.display.ChatFormatter;

public class RankChatPreviewGUI {
    public void open(Player player, String rankId) {
        Rank rank = Rank.getById(rankId);
        if (rank == null) {
            player.sendMessage("§cРанг не найден: " + rankId);
            return;
        }

        // Пример сообщения
        String preview = ChatFormatter.formatChat(rank, player.getName(), "Пример сообщения в чате");

        // Показать предпросмотр в чате
        player.sendMessage("§7--- §eФормат чата для ранга §f" + rank.getDisplayName() + " §7---");
        player.sendMessage(preview);
        player.sendMessage("§7------------------------------");

        // Если игрок — администратор, показать ссылку на WebX Panel
        if (player.hasPermission("rank.admin.manage-ranks") || player.isOp()) {
            player.sendMessage("§bНастроить форматирование чата для ранга можно через WebX Panel:");
            player.sendMessage("§9http://localhost:8080/dashboard/ranks/" + rank.getId());
        }
    }
}
