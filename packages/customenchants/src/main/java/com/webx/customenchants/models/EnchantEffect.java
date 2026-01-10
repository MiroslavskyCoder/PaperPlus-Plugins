package com.webx.customenchants.models;

import java.util.HashMap;
import java.util.Map;

public class EnchantEffect {
    private final String name;
    private final Map<String, Object> effects = new HashMap<>();
    
    public EnchantEffect(String name) {
        this.name = name;
    }
    
    public void addEffect(String key, Object value) {
        effects.put(key, value);
    }
    
    public Object getEffect(String key) {
        return effects.get(key);
    }
    
    public String getName() { return name; }
}
