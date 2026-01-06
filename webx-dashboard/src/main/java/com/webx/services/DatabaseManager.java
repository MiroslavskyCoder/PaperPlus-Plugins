package com.webx.services; 

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
 
public class DatabaseManager {

    private HikariDataSource dataSource;
    private final JavaPlugin plugin;
    private final String jdbcUrl;

    public DatabaseManager(JavaPlugin plugin, String host, int port, String database, String user, String password) {
        this.plugin = plugin;
        this.jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        
        HikariConfig config = new HikariConfig();
        
        // Настройки подключения
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        config.setMaximumPoolSize(10); 
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);  
        config.setIdleTimeout(600000); 
        
        this.dataSource = new HikariDataSource(config);
        
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            
            String createTableSQL = 
                "CREATE TABLE IF NOT EXISTS user_profiles (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "health INTEGER, " +
                "max_health INTEGER, " +
                "food_level INTEGER, " +
                "experience REAL, " + 
                "level INTEGER, " +
                "world_name VARCHAR(50), " +
                "x DOUBLE PRECISION, " +
                "y DOUBLE PRECISION, " +
                "z DOUBLE PRECISION, " +
                "yaw REAL, " +
                "pitch REAL, " +
                "is_authenticated BOOLEAN NOT NULL, " +
                "last_save TIMESTAMP WITHOUT TIME ZONE" +
                ")";
            
            stmt.execute(createTableSQL);
            plugin.getLogger().info("PostgreSQL: check/create user_profiles table initialized.");
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Error initializing database: " + e.getMessage());
            plugin.getLogger().severe("Check your PostgreSQL settings (host, port, user, password, database).");
            e.printStackTrace();
        }
    }
 
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Pool Connection to PostgreSQL closed or not initialized.");
        }
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Pool connections to PostgreSQL (HikariCP) closed.");
        }
    }
}