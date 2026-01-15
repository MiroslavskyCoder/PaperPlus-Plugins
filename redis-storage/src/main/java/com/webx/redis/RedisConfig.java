package com.webx.redis;

public class RedisConfig {
    public String host = "localhost";
    public int port = 6379;
    public String password = "";
    public int database = 0;
    public boolean ssl = false;
    public int timeoutMillis = 2000;
    public int socketTimeoutMillis = 2000;

    public RedisConfig() {
    }

    public RedisConfig(String host, int port, String password, int database, boolean ssl) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.database = database;
        this.ssl = ssl;
    }
}
