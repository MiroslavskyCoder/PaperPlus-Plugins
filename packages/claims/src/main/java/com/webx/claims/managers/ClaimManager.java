package com.webx.claims.managers;

import com.webx.claims.models.Claim;
import org.bukkit.Location;
import java.util.*;

public class ClaimManager {
    private final Map<String, Claim> claims = new HashMap<>();
    
    public void createClaim(String id, Location pos1, Location pos2) {
        claims.put(id, new Claim(id, pos1, pos2));
    }
    
    public Claim getClaim(String id) {
        return claims.get(id);
    }
    
    public Collection<Claim> getAllClaims() {
        return claims.values();
    }
}
