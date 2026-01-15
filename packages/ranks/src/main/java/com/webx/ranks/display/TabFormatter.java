package com.webx.ranks.display;

import com.webx.ranks.models.Rank;

public class TabFormatter {
    /**
     * Форматирует отображение игрока в табе с учетом ранга, цвета и шаблона.
     */
    public static String formatTab(Rank rank, String playerName) {
        if (rank == null) return playerName;
        String color = rank.getTabColor() != null ? rank.getTabColor() : (rank.getColor() != null ? rank.getColor() : "");
        String prefix = rank.getPrefix() != null ? rank.getPrefix() : "";
        String suffix = rank.getSuffix() != null ? rank.getSuffix() : "";
        // Кастомный шаблон (можно расширить)
        String format = "%prefix%%player%%suffix%";
        format = format.replace("%prefix%", prefix)
                       .replace("%player%", playerName)
                       .replace("%suffix%", suffix);
        if (!color.isEmpty()) {
            format = color + format;
        }
        return format.replace("&", "§");
    }
}
