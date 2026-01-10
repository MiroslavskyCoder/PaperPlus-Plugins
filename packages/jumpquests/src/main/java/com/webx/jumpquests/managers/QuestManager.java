package com.webx.jumpquests.managers;

import com.webx.jumpquests.models.Quest;
import java.util.*;

public class QuestManager {
    private final Map<String, Quest> quests = new HashMap<>();
    
    public void addQuest(String id, Quest quest) {
        quests.put(id, quest);
    }
    
    public Quest getQuest(String id) {
        return quests.get(id);
    }
    
    public Collection<Quest> getAllQuests() {
        return quests.values();
    }
}
