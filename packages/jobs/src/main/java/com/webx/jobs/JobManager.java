package com.webx.jobs;

import org.bukkit.entity.Player;
import java.util.*;

public class JobManager {
    private Map<UUID, String> playerJob = new HashMap<>();
    private Map<UUID, Integer> jobLevel = new HashMap<>();
    
    public void setJob(Player player, String jobName) {
        UUID uuid = player.getUniqueId();
        playerJob.put(uuid, jobName);
        jobLevel.put(uuid, 1);
        player.sendMessage("§aJob set to §f" + jobName);
    }
    
    public String getJob(Player player) {
        return playerJob.getOrDefault(player.getUniqueId(), "None");
    }
    
    public int getJobLevel(Player player) {
        return jobLevel.getOrDefault(player.getUniqueId(), 1);
    }
    
    public void addJobExp(Player player, int exp) {
        player.sendMessage("§b+§f" + exp + " Job EXP");
    }
}
