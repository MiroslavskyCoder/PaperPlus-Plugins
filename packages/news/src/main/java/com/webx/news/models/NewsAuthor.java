package com.webx.news.models;

import java.util.UUID;

public class NewsAuthor {
    private final UUID uuid;
    private final String name;
    
    public NewsAuthor(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
    
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
}
