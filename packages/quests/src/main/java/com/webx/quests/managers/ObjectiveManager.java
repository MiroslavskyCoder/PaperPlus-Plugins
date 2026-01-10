package com.webx.quests.managers;

import com.webx.quests.QuestsPlugin;

public class ObjectiveManager {
    private final QuestsPlugin plugin;

    public ObjectiveManager(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean checkObjective(String objectiveType, Object... args) {
        return switch (objectiveType) {
            case "kill" -> (int) args[0] > 0;
            case "break" -> (int) args[0] > 0;
            case "collect" -> (int) args[0] > 0;
            case "location" -> (boolean) args[0];
            default -> false;
        };
    }

    public String getObjectiveDescription(String objectiveType) {
        return switch (objectiveType) {
            case "kill" -> "Kill enemies";
            case "break" -> "Break blocks";
            case "collect" -> "Collect items";
            case "location" -> "Reach a location";
            default -> "Unknown objective";
        };
    }
}
