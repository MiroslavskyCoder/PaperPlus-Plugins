package com.webx.ranks.display;

// import me.clip.placeholderapi.PlaceholderAPI; // Uncomment if PlaceholderAPI is present

import com.webx.ranks.models.Rank;

public class ChatFormatter {
    /**
     * Форматирует сообщение чата с учетом ранга, цветов, PlaceholderAPI и шаблонов.
     * @param rank ранг
     * @param playerName имя игрока
     * @param message текст сообщения
     * @return отформатированная строка
     */
    public static String formatChat(Rank rank, String playerName, String message) {
        if (rank == null) return playerName + ": " + message;
        String chatColor = rank.getChatColor() != null ? rank.getChatColor() : (rank.getColor() != null ? rank.getColor() : "");
        String prefix = rank.getPrefix() != null ? rank.getPrefix() : "";
        String suffix = rank.getSuffix() != null ? rank.getSuffix() : "";
        // Кастомный шаблон (можно расширить)
        String format = "%prefix%%player%%suffix%&7: %message%";
        format = format.replace("%prefix%", prefix)
                       .replace("%player%", playerName)
                       .replace("%suffix%", suffix)
                       .replace("%message%", message);
        if (!chatColor.isEmpty()) {
            format = chatColor + format;
        }
        // Заменить & на § для Minecraft цветов
        format = format.replace("&", "§");
        // PlaceholderAPI hook (если установлен)
            // if (isPlaceholderApiPresent()) {
            //     format = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, format);
            // }
        return format;
    }

    private static boolean isPlaceholderApiPresent() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
