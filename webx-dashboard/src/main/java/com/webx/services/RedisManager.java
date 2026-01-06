package com.webx.services;

import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    private final JedisPool jedisPool;
    private final JavaPlugin plugin;

    public RedisManager(JavaPlugin plugin, String host, int port, String password) {
        this.plugin = plugin;
        
        JedisPoolConfig poolConfig = new JedisPoolConfig(); 
        poolConfig.setMaxTotal(20); 
        poolConfig.setMaxIdle(10);

        this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        
        try (redis.clients.jedis.Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
            plugin.getLogger().info("Successfully connected to Redis on " + host + ":" + port);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to connect to Redis: " + e.getMessage()); 
        }
    }

    /**
     * Method to get the JedisPool instance
     * 
     * @return JedisPool instance that can be used to get Jedis instances
     */
    public JedisPool getPool() {
        return jedisPool;
    }

    public void shutdown() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            plugin.getLogger().info("Pull connections to Redis closed.");
        }
    }
}