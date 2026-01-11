package com.webx.bounties;

import org.bukkit.entity.Player;
import java.util.*;

public class BountyManager {
    private Map<UUID, Integer> bounties = new HashMap<>();
    
    public void setBounty(UUID target, int amount) {
        bounties.put(target, amount);
    }
    
    public int getBounty(UUID target) {
        return bounties.getOrDefault(target, 0);
    }
    
    public boolean hasBounty(UUID target) {
        return bounties.containsKey(target);
    }
    
    public void removeBounty(UUID target) {
        bounties.remove(target);
    }
    
    public Map<UUID, Integer> getAllBounties() {
        return new HashMap<>(bounties);
    }
}
