package com.webx.afk.database;

import com.webx.afk.models.AFKPlayer;
import java.util.Map;
import java.util.UUID;

public interface StorageProvider {
    void initialize();
    void close();
    void savePlayer(AFKPlayer player);
    void loadPlayers(Map<UUID, AFKPlayer> players);
    void deletePlayer(UUID uuid);
}
