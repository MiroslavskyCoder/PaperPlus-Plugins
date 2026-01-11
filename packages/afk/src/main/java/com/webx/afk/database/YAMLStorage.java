package com.webx.afk.database;

import com.webx.afk.models.AFKPlayer;
import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

public class YAMLStorage implements StorageProvider {
    private final Path dataFolder;
    
    public YAMLStorage(Path dataFolder) {
        this.dataFolder = dataFolder;
    }
    
    @Override
    public void initialize() {
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void close() {
        // YAML storage doesn't need cleanup
    }
    
    @Override
    public void savePlayer(AFKPlayer player) {
        // Сохранение в YAML
    }
    
    @Override
    public void loadPlayers(Map<UUID, AFKPlayer> players) {
        // Загрузка из YAML
    }
    
    @Override
    public void deletePlayer(UUID uuid) {
        // Удаление из YAML
    }
}
