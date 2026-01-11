package com.webx.playerinfo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EconomyDataManager {
    
    private static final Gson gson = new Gson();
    private final File accountsFile;
    private Map<String, Double> playerAccounts;
    
    public EconomyDataManager(File dataFolder) {
        this.accountsFile = new File(dataFolder, "accounts.json");
        this.playerAccounts = new HashMap<>();
        loadAccounts();
    }
    
    private void loadAccounts() {
        if (!accountsFile.exists()) {
            playerAccounts = new HashMap<>();
            return;
        }
        
        try (FileReader reader = new FileReader(accountsFile)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            
            for (String uuid : json.keySet()) {
                try {
                    JsonObject accountData = json.getAsJsonObject(uuid);
                    double balance = accountData.has("balance") ? accountData.get("balance").getAsDouble() : 0.0;
                    playerAccounts.put(uuid, balance);
                } catch (Exception e) {
                    // Skip invalid entries
                }
            }
        } catch (IOException e) {
            playerAccounts = new HashMap<>();
        }
    }
    
    public double getBalance(Player player) {
        return playerAccounts.getOrDefault(player.getUniqueId().toString(), 0.0);
    }
    
    public void refreshAccounts() {
        loadAccounts();
    }
}
