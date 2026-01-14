package com.webx.quests.models;

public enum QuestDifficulty {
    EASY("Легкий", 1.0, "§a"),
    MEDIUM("Средний", 1.5, "§e"),
    HARD("Сложный", 2.0, "§6"),
    EXPERT("Экспертный", 3.0, "§c"),
    LEGENDARY("Легендарный", 5.0, "§5");

    private final String displayName;
    private final double rewardMultiplier;
    private final String colorCode;

    QuestDifficulty(String displayName, double rewardMultiplier, String colorCode) {
        this.displayName = displayName;
        this.rewardMultiplier = rewardMultiplier;
        this.colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getFormattedName() {
        return colorCode + displayName + "§r";
    }
}
