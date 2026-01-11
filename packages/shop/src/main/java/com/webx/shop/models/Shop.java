package com.webx.shop.models;

import java.util.*;

public class Shop {
    private final String name;
    private final UUID owner;
    private final Map<String, ShopItem> items;
    private boolean enabled;

    public Shop(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.items = new HashMap<>();
        this.enabled = true;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void addItem(ShopItem item) {
        items.put(item.getId(), item);
    }

    public void removeItem(String itemId) {
        items.remove(itemId);
    }

    public ShopItem getItem(String itemId) {
        return items.get(itemId);
    }

    public Collection<ShopItem> getItems() {
        return items.values();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getItemCount() {
        return items.size();
    }
}
