package com.webx.player;

import com.webx.services.RedisManager;
import com.webx.services.DatabaseManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AuthManager {

    private final RedisManager redisManager;
    private final DatabaseManager databaseManager;
    
    private static final int PERMANENT_TTL_SECONDS = 60 * 60 * 24 * 30; 

    public AuthManager(RedisManager redisManager, DatabaseManager databaseManager) {
        this.redisManager = redisManager;
        this.databaseManager = databaseManager;
    }

    public void setAuthorized(Player player) {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String redisKey = "auth:user:" + uuid;
        
        String status = "AUTHORIZED"; 
        
        // Save to Redis for fast access
        try (Jedis jedis = redisManager.getPool().getResource()) { 
            jedis.setex(redisKey, PERMANENT_TTL_SECONDS, status);
        }
        
        // Save to Database for persistent storage
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO user_profiles (uuid, name, is_authenticated, last_save) " +
                 "VALUES (?, ?, true, ?) " +
                 "ON CONFLICT (uuid) DO UPDATE SET is_authenticated = true, last_save = ?")) {
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            pstmt.setString(1, uuid);
            pstmt.setString(2, name);
            pstmt.setTimestamp(3, now);
            pstmt.setTimestamp(4, now);
            pstmt.executeUpdate();
            
            System.out.println("Установлен статус авторизации для " + name + " (Redis + DB)");
        } catch (SQLException e) {
            System.err.println("Ошибка сохранения авторизации в БД для " + name + ": " + e.getMessage());
        }
    }

    public boolean isAuthorized(OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        String redisKey = "auth:user:" + uuid;
        
        // Check Redis first (fast cache)
        try (Jedis jedis = redisManager.getPool().getResource()) { 
            if (jedis.exists(redisKey)) {
                return true;
            }
        }
        
        // If not in Redis, check Database (persistent storage)
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT is_authenticated FROM user_profiles WHERE uuid = ?")) {
            
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                boolean isAuth = rs.getBoolean("is_authenticated");
                
                // Restore to Redis if found in DB
                if (isAuth) {
                    try (Jedis jedis = redisManager.getPool().getResource()) {
                        jedis.setex(redisKey, PERMANENT_TTL_SECONDS, "AUTHORIZED");
                    }
                }
                
                return isAuth;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка проверки авторизации в БД для " + uuid + ": " + e.getMessage());
        }
        
        return false;
    }
    
    public void clearAuthorization(OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        String redisKey = "auth:user:" + uuid;
        
        // Clear from Redis
        try (Jedis jedis = redisManager.getPool().getResource()) {
            jedis.del(redisKey);
        }
        
        // Clear from Database
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE user_profiles SET is_authenticated = false, last_save = ? WHERE uuid = ?")) {
            
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(2, uuid);
            pstmt.executeUpdate();
            
            System.out.println("Удалена авторизация для " + player.getName() + " (Redis + DB)");
        } catch (SQLException e) {
            System.err.println("Ошибка удаления авторизации в БД для " + uuid + ": " + e.getMessage());
        }
    }
}