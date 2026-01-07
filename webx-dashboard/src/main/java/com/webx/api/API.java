package com.webx.api;

public class API {
    
    static final String BASE_PATH = "/api";

    static public String getFullPath(String endpoint) {
        return BASE_PATH + "/" + endpoint;
    }
    
}
