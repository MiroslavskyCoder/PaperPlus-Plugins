package com.webx.homesextended.models;

import java.time.LocalDateTime;

public class HomeSettings {
    private final String homeName;
    private boolean publicAccess;
    private final LocalDateTime createdAt;
    
    public HomeSettings(String homeName) {
        this.homeName = homeName;
        this.publicAccess = false;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getHomeName() { return homeName; }
    public boolean isPublic() { return publicAccess; }
    public void setPublic(boolean pub) { this.publicAccess = pub; }
}
