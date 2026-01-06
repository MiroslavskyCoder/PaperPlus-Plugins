package com.webx.core;
 
import org.bukkit.plugin.java.JavaPlugin;

import com.webx.api.RouterProvider; 

public class Server {

    private String name;
    private String address;
    private int port;
    private String version;

    private RouterProvider routerProvider;

    public Server() { }

    public void setFromPluginAllow(JavaPlugin plugin) { 
        this.routerProvider = new RouterProvider(plugin);
        this.name = plugin.getServer().getName();
        this.address = plugin.getServer().getIp();
        this.port = plugin.getServer().getPort();
        this.version = plugin.getServer().getMinecraftVersion();

        plugin.getLogger().info("Server info set from plugin:");
        plugin.getLogger().info("Name: " + name);
        plugin.getLogger().info("Address: " + address);
        plugin.getLogger().info("Port: " + port);
        plugin.getLogger().info("Version: " + version);

    } 


    // Геттеры
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getPort() { return port; }
    public String getVersion() { return version; }

    // Класс для данных метрик
    public static class MetricsData {
        public long timestamp;
        public double cpuUsage;
        public double memoryUsage;
        public int onlinePlayers;

        public MetricsData(long timestamp, double cpuUsage, double memoryUsage, int onlinePlayers) {
            this.timestamp = timestamp;
            this.cpuUsage = cpuUsage;
            this.memoryUsage = memoryUsage;
            this.onlinePlayers = onlinePlayers;
        }
    }
}
