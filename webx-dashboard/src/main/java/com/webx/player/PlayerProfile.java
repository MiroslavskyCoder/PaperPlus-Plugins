package com.webx.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import com.webx.services.DatabaseManager;

public class PlayerProfile {

    public final UUID uuid;
    
    public String name; 
    public int health;
    public int maxHealth;
    public int foodLevel;
    public float experience;
    public int level;
    public String worldName;
    public double x, y, z;
    public float yaw, pitch;
    public boolean isAuthenticated = false; 

    public PlayerProfile(UUID uuid, String name, int health, boolean isAuthenticated) {
        this.uuid = uuid;
        this.name = name;
        this.health = health; 
        this.isAuthenticated = isAuthenticated;
    }

    public PlayerProfile(org.bukkit.entity.Player bukkitPlayer) {
        this.uuid = bukkitPlayer.getUniqueId(); 
        this.name = bukkitPlayer.getName();
        this.updateOnlineData(bukkitPlayer);
    }
    
    public boolean isOnline() {
        return org.bukkit.Bukkit.getPlayer(uuid) != null;
    }
    
    public void updateOnlineData(org.bukkit.entity.Player bukkitPlayer) {
        if (bukkitPlayer == null) return;
        
        this.name = bukkitPlayer.getName();
        this.health = (int) bukkitPlayer.getHealth();
        this.maxHealth = (int) bukkitPlayer.getMaxHealth();
        this.foodLevel = bukkitPlayer.getFoodLevel();
        this.experience = bukkitPlayer.getExp();
        this.level = bukkitPlayer.getLevel();
        this.worldName = bukkitPlayer.getWorld().getName();
        this.x = bukkitPlayer.getLocation().getX();
        this.y = bukkitPlayer.getLocation().getY();
        this.z = bukkitPlayer.getLocation().getZ();
        this.yaw = bukkitPlayer.getLocation().getYaw();
        this.pitch = bukkitPlayer.getLocation().getPitch();
    } 
    
    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getHealth() { 
        return isOnline() ? (int) org.bukkit.Bukkit.getPlayer(uuid).getHealth() : health;
    }
    

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.isAuthenticated = authenticated;
    }
    
    public void saveToDatabase(DatabaseManager dbService) {
        if (dbService == null) { 
            return; 
        }
        
        if (isOnline()) {
            org.bukkit.entity.Player bukkitPlayer = org.bukkit.Bukkit.getPlayer(uuid);
            if (bukkitPlayer != null) {
                updateOnlineData(bukkitPlayer);
            }
        }

        final String SQL = 
            "INSERT INTO user_profiles (uuid, name, health, max_health, food_level, experience, level, world_name, x, y, z, yaw, pitch, is_authenticated, last_save) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (uuid) DO UPDATE SET " +
            "name = EXCLUDED.name, " +
            "health = EXCLUDED.health, " +
            "max_health = EXCLUDED.max_health, " +
            "food_level = EXCLUDED.food_level, " +
            "experience = EXCLUDED.experience, " +
            "level = EXCLUDED.level, " +
            "world_name = EXCLUDED.world_name, " +
            "x = EXCLUDED.x, " +
            "y = EXCLUDED.y, " +
            "z = EXCLUDED.z, " +
            "yaw = EXCLUDED.yaw, " +
            "pitch = EXCLUDED.pitch, " +
            "is_authenticated = EXCLUDED.is_authenticated, " +
            "last_save = EXCLUDED.last_save";

        try (Connection conn = dbService.getConnection();
            PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setInt(3, health);
            ps.setInt(4, maxHealth);
            ps.setInt(5, foodLevel);
            ps.setFloat(6, experience);
            ps.setInt(7, level);
            ps.setString(8, worldName);
            ps.setDouble(9, x);
            ps.setDouble(10, y);
            ps.setDouble(11, z);
            ps.setFloat(12, yaw);
            ps.setFloat(13, pitch);
            ps.setBoolean(14, isAuthenticated);
            ps.setTimestamp(15, Timestamp.from(Instant.now()));

            ps.executeUpdate();
            System.out.println("Профиль игрока " + name + " сохранен в PostgreSQL.");

        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    }
}