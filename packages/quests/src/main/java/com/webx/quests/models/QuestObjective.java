package com.webx.quests.models;

public class QuestObjective {
    private String id;
    private QuestType type;
    private String description;
    private String target; // mob type, block type, item type, etc.
    private int required;
    private String location; // optional location requirement
    private boolean optional;

    public QuestObjective(String id, QuestType type, String description, String target, int required) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.target = target;
        this.required = required;
        this.optional = false;
    }

    public String getId() {
        return id;
    }

    public QuestType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getTarget() {
        return target;
    }

    public int getRequired() {
        return required;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
