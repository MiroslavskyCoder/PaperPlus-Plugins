package com.webx.warps.storage;

import com.webx.warps.models.Warp;

import java.util.List;

public interface StorageProvider {
    void initialize();
    void close();
    
    List<Warp> loadWarps();
    void saveWarps(List<Warp> warps);
    void saveWarp(Warp warp);
    void deleteWarp(String name);
}
