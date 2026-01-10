package com.webx.customitems;

import org.bukkit.inventory.ItemStack;
import java.util.*;

public class CustomItemsManager {
    private Map<String, CustomItem> items = new HashMap<>();
    
    public void registerItem(String id, CustomItem item) {
        items.put(id, item);
    }
    
    public CustomItem getItem(String id) {
        return items.get(id);
    }
    
    public Collection<CustomItem> getAllItems() {
        return items.values();
    }
    
    static class CustomItem {
        String id;
        String displayName;
        ItemStack stack;
        
        public CustomItem(String id, String displayName, ItemStack stack) {
            this.id = id;
            this.displayName = displayName;
            this.stack = stack;
        }
    }
}
