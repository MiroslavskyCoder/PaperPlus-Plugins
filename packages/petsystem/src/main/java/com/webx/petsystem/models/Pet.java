package com.webx.petsystem.models;

import java.util.UUID;

public class Pet {
    private final UUID uuid;
    private final UUID ownerUuid;
    private final String name;
    private final String type;
    private int level;
    
    public Pet(UUID uuid, UUID ownerUuid, String name, String type) {
        this.uuid = uuid;
        this.ownerUuid = ownerUuid;
        this.name = name;
        this.type = type;
        this.level = 1;
    }
    
    public UUID getUuid() { return uuid; }
    public UUID getOwnerUuid() { return ownerUuid; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getLevel() { return level; }
    public void levelUp() { this.level++; }
}
