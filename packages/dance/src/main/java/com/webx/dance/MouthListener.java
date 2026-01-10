package com.webx.dance;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MouthListener implements Listener {
    private final MouthAnimator animator;
    private final JavaPlugin plugin;

    public MouthListener(JavaPlugin plugin, MouthAnimator animator){
        this.plugin = plugin;
        this.animator = animator;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        int dur = plugin.getConfig().getInt("settings.mouth.duration-ticks", 40);
        animator.startTalking(e.getPlayer(), dur);
    }
}
