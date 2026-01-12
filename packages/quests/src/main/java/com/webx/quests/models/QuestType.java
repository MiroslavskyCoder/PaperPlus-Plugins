package com.webx.quests.models;

public enum QuestType {
    KILL("Убийство", "kill"),
    COLLECT("Сбор предметов", "collect"),
    BREAK("Ломание блоков", "break"),
    PLACE("Размещение блоков", "place"),
    CRAFT("Крафт предметов", "craft"),
    EXPLORE("Исследование", "explore"),
    TALK("Разговор с NPC", "talk"),
    FISH("Рыбалка", "fish"),
    TRADE("Торговля", "trade"),
    FARM("Фермерство", "farm"),
    MINE("Добыча руды", "mine"),
    BUILD("Строительство", "build"),
    TAME("Приручение", "tame"),
    BREED("Разведение", "breed"),
    ENCHANT("Зачарование", "enchant"),
    TRAVEL("Путешествие", "travel"),
    DEFEND("Защита", "defend"),
    COMPLETE_DUNGEON("Прохождение подземелья", "dungeon"),
    WIN_MINIGAME("Победа в мини-игре", "minigame"),
    DAILY("Ежедневное задание", "daily");

    private final String displayName;
    private final String key;

    QuestType(String displayName, String key) {
        this.displayName = displayName;
        this.key = key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getKey() {
        return key;
    }

    public static QuestType fromKey(String key) {
        for (QuestType type : values()) {
            if (type.key.equalsIgnoreCase(key)) {
                return type;
            }
        }
        return KILL; // default
    }
}
