package com.webx.ranks.permissions;

import com.webx.ranks.models.Rank;

public class PermissionManager {
    public boolean hasPermission(Rank rank, String permission) {
        // Проверка прав с учетом наследования
        if (rank == null) return false;
        if (rank.getPermissions().contains(permission)) return true;
        // Проверить наследование
        if (rank.getParent() != null) {
            // Получить родительский ранг и проверить у него
            Rank parent = Rank.getById(rank.getParent());
            if (parent != null) {
                return hasPermission(parent, permission);
            }
        }
        return false;
    }

    public void togglePermission(Rank rank, String permission, boolean enable) {
        if (rank == null) return;
        if (enable) {
            rank.getPermissions().add(permission);
        } else {
            rank.getPermissions().remove(permission);
        }
    }
}
