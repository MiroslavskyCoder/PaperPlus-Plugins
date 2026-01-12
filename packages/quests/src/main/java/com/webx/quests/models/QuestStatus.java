package com.webx.quests.models;

public enum QuestStatus {
    NOT_STARTED("Не начат", "§7"),
    IN_PROGRESS("В процессе", "§e"),
    COMPLETED("Завершен", "§a"),
    FAILED("Провален", "§c"),
    CLAIMED("Награда получена", "§b");

    private final String displayName;
    private final String colorCode;

    QuestStatus(String displayName, String colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getFormattedName() {
        return colorCode + displayName + "§r";
    }
}
