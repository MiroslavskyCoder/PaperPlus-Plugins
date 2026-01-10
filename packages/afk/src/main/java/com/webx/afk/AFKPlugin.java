package com.webx.afk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AFKPlugin extends JavaPlugin implements Listener {
    private static AFKPlugin instance;
    private Map<UUID, Long> lastActivity = new HashMap<>();
    private Map<UUID, Boolean> afkStatus = new HashMap<>();
    
    private long afkTimeoutMillis;
    private String afkPrefix;
    private String afkSuffix;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        afkTimeoutMillis = getConfig().getLong("afk-timeout-minutes", 5) * 60 * 1000;
        afkPrefix = getConfig().getString("afk-prefix", "&7[AFK] &r");
        afkSuffix = getConfig().getString("afk-suffix", " &7(AFK)");
        
        getServer().getPluginManager().registerEvents(this, this);
        
        // Проверка AFK каждую минуту
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::checkAFKPlayers, 60, 1200);
        
        getLogger().info("AFK Plugin enabled!");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Обновляем время последней активности при движении
        lastActivity.put(uuid, System.currentTimeMillis());
        
        // Снимаем AFK статус если игрок был AFK
        if (afkStatus.getOrDefault(uuid, false)) {
            afkStatus.put(uuid, false);
            broadcastAFKStatus(player, false);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        lastActivity.remove(uuid);
        afkStatus.remove(uuid);
    }
    
    private void checkAFKPlayers() {
        long now = System.currentTimeMillis();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            long lastActive = lastActivity.getOrDefault(uuid, now);
            boolean isAFK = afkStatus.getOrDefault(uuid, false);
            
            if (now - lastActive > afkTimeoutMillis && !isAFK) {
                // Игрок стал AFK
                afkStatus.put(uuid, true);
                broadcastAFKStatus(player, true);
            }
        }
    }
    
    private void broadcastAFKStatus(Player player, boolean afk) {
        String message = afk 
            ? afkPrefix + player.getName() + afkSuffix + " &7is now AFK"
            : "&7" + player.getName() + " &ris back!";
        
        Bukkit.broadcastMessage(colorize(message));
    }
    
    private String colorize(String text) {
        return text.replace("&", "§");
    }
    
    public static AFKPlugin getInstance() {
        return instance;
    }
    
    public boolean isAFK(Player player) {
        return afkStatus.getOrDefault(player.getUniqueId(), false);
    }
}
