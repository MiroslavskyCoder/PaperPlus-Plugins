package com.webx.dungeonraids.managers;

import com.webx.dungeonraids.models.Dungeon;
import java.util.*;

public class DungeonManager {
    private final Map<String, Dungeon> dungeons = new HashMap<>();
    
    public void registerDungeon(String name, Dungeon dungeon) {
        dungeons.put(name, dungeon);
    }
    
    public Dungeon getDungeon(String name) {
        return dungeons.get(name);
    }
}
