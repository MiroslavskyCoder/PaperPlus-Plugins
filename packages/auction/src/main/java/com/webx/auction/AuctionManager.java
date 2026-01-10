package com.webx.auction;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class AuctionManager {
    private Map<UUID, List<AuctionListing>> listings = new HashMap<>();
    
    public void createListing(Player player, ItemStack item, long price) {
        UUID uuid = player.getUniqueId();
        listings.computeIfAbsent(uuid, k -> new ArrayList<>())
            .add(new AuctionListing(item, price, System.currentTimeMillis()));
        player.sendMessage("§aAuction created!");
    }
    
    public List<AuctionListing> getListings(UUID seller) {
        return listings.getOrDefault(seller, new ArrayList<>());
    }
    
    public Collection<AuctionListing> getAllListings() {
        List<AuctionListing> all = new ArrayList<>();
        listings.values().forEach(all::addAll);
        return all;
    }
    
    static class AuctionListing {
        ItemStack item;
        long price;
        long createdAt;
        
        AuctionListing(ItemStack item, long price, long createdAt) {
            this.item = item;
            this.price = price;
            this.createdAt = createdAt;
        }
    }
}
