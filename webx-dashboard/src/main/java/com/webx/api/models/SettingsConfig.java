package com.webx.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingsConfig {
    
    @JsonProperty
    public AuthPlayerSettings authPlayer;
    
    @JsonProperty
    public SQLConfig sqlConfig;
    
    @JsonProperty
    public RedisConfig redisConfig;
    
    @JsonProperty
    public String curseforgeApiKey = null;
    
    public SettingsConfig() {
        this.authPlayer = new AuthPlayerSettings();
        this.sqlConfig = new SQLConfig();
        this.redisConfig = new RedisConfig();
    }
    
    public static class AuthPlayerSettings {
        @JsonProperty("isAuthPlayerEnabled")
        public boolean isAuthPlayerEnabled = false;
        
        @JsonProperty("inputMask")
        public String inputMask = "NN-AAA-999";
        
        public AuthPlayerSettings() {}
    }
    
    public static class SQLConfig {
        @JsonProperty
        public String host = "localhost";
        
        @JsonProperty
        public int port = 5432;
        
        @JsonProperty
        public String database = "webx_dashboard";
        
        @JsonProperty
        public String username = "postgres";
        
        @JsonProperty
        public String password = "";
        
        @JsonProperty
        public boolean ssl = false;
        
        public SQLConfig() {}
    }
    
    public static class RedisConfig {
        @JsonProperty
        public String host = "localhost";
        
        @JsonProperty
        public int port = 6379;
        
        @JsonProperty
        public String password = "";
        
        @JsonProperty
        public int db = 0;
        
        public RedisConfig() {}
    }
}
