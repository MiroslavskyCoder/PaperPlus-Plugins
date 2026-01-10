package com.webx.chatformatting.managers;

import java.util.*;

public class ChannelManager {
    private final Map<String, com.webx.chatformatting.models.ChatChannel> channels = new HashMap<>();
    
    public void registerChannel(com.webx.chatformatting.models.ChatChannel channel) {
        channels.put(channel.getName(), channel);
    }
    
    public com.webx.chatformatting.models.ChatChannel getChannel(String name) {
        return channels.get(name);
    }
}
