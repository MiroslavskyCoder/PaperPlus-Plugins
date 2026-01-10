package com.webx.quests.managers;

import com.webx.quests.QuestsPlugin;
import com.webx.quests.models.Quest;

import java.util.*;

public class QuestManager {
    private final QuestsPlugin plugin;
    private final Map<String, Quest> quests;

    public QuestManager(QuestsPlugin plugin) {
        this.plugin = plugin;
        this.quests = new HashMap<>();
    }

    public void loadQuests() {
        quests.clear();
        // TODO: Load from storage
        plugin.getLogger().info("Loaded " + quests.size() + " quests");
    }

    public Quest getQuest(String id) {
        return quests.get(id);
    }

    public void addQuest(Quest quest) {
        quests.put(quest.getId(), quest);
    }

    public void removeQuest(String id) {
        quests.remove(id);
    }

    public Collection<Quest> getAllQuests() {
        return quests.values();
    }

    public int getQuestCount() {
        return quests.size();
    }
}
