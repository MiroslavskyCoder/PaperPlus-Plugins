package com.webx.insurance;

import org.bukkit.entity.Player;
import java.util.*;

public class InsuranceManager {
    private Map<UUID, InsurancePolicy> policies = new HashMap<>();
    
    public void createPolicy(Player player, double amount, long duration) {
        policies.put(player.getUniqueId(), new InsurancePolicy(amount, duration));
        player.sendMessage("§aInsurance policy created for $" + amount);
    }
    
    public void claimInsurance(Player player) {
        InsurancePolicy policy = policies.get(player.getUniqueId());
        if (policy != null && policy.isActive()) {
            player.sendMessage("§aInsurance claim approved! +$" + policy.amount);
            policies.remove(player.getUniqueId());
        }
    }
    
    static class InsurancePolicy {
        double amount;
        long expiresAt;
        
        InsurancePolicy(double amount, long duration) {
            this.amount = amount;
            this.expiresAt = System.currentTimeMillis() + duration;
        }
        
        boolean isActive() {
            return System.currentTimeMillis() < expiresAt;
        }
    }
}
