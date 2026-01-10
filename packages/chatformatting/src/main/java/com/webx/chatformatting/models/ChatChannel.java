package com.webx.chatformatting.models;

public class ChatChannel {
    private final String name;
    private final String prefix;
    private final String color;
    
    public ChatChannel(String name, String prefix, String color) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;
    }
    
    public String getName() { return name; }
    public String getPrefix() { return prefix; }
    public String getColor() { return color; }
}
