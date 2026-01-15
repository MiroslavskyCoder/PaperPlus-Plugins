package com.webx.clans.roles;

import java.util.Set;

public class ClanRoleDefinition {
    private String name;
    private Set<String> permissions;
    private int priority;
    // TODO: Add display name, color, etc.

    public ClanRoleDefinition(String name, Set<String> permissions, int priority) {
        this.name = name;
        this.permissions = permissions;
        this.priority = priority;
    }
    // Getters and setters...
}
