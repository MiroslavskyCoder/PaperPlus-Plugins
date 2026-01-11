package com.webx.afk.utils;

import com.webx.afk.models.AFKPlayer;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class StorageManager {
    private final Path dataFile;
    
    public StorageManager(Path pluginFolder) throws IOException {
        this.dataFile = pluginFolder.resolve("afk_data.txt");
        Files.createDirectories(pluginFolder);
    }
    
    public void saveAFKData(Map<UUID, AFKPlayer> players) throws IOException {
        List<String> lines = new ArrayList<>();
        for (AFKPlayer player : players.values()) {
            lines.add(player.getUUID() + ":" + player.isAFK() + ":" + player.getAFKCounter());
        }
        Files.write(dataFile, lines);
    }
    
    public void loadAFKData() throws IOException {
        if (!Files.exists(dataFile)) return;
        Files.lines(dataFile).forEach(line -> {
            // Парсить данные
        });
    }
}
