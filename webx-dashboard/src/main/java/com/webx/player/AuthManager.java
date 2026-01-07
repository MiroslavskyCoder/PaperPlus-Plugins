package com.webx.player;

import com.webx.services.RedisManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

public class AuthManager {

    private final RedisManager redisManager;
    
    private static final int PERMANENT_TTL_SECONDS = 60 * 60 * 24 * 30; 

    public AuthManager(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public void setAuthorized(Player player) {
        String uuid = player.getUniqueId().toString();
        String redisKey = "auth:user:" + uuid;
        
        String status = "AUTHORIZED"; 
        
        try (Jedis jedis = redisManager.getPool().getResource()) { 
            jedis.setex(redisKey, PERMANENT_TTL_SECONDS, status);
            System.out.println("Установлен статус авторизации для " + player.getName());
        }
    }

    public boolean isAuthorized(OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        String redisKey = "auth:user:" + uuid;
        
        try (Jedis jedis = redisManager.getPool().getResource()) { 
            return jedis.exists(redisKey);
        }
    }
    
    public void clearAuthorization(OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        String redisKey = "auth:user:" + uuid;
        
        try (Jedis jedis = redisManager.getPool().getResource()) {
            jedis.del(redisKey);
            System.out.println("Удалена авторизация для " + player.getName());
        }
    }
}