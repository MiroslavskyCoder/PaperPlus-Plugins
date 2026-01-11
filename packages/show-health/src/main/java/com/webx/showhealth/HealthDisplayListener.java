package com.webx.showhealth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.attribute.Attribute;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HealthDisplayListener implements Listener {
    private final ShowHealthPlugin plugin;
    private final Map<UUID, Boolean> playerHealthToggle = new HashMap<>();

    public HealthDisplayListener(ShowHealthPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            updateEntityHealthDisplay((LivingEntity) event.getEntity());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                updateEntityHealthDisplay((LivingEntity) event.getEntity());
            }, 1L);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        entity.customName(Component.text(""));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerHealthToggle.put(player.getUniqueId(), true);
    }

    public void toggleHealthForPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        boolean enabled = playerHealthToggle.getOrDefault(uuid, true);
        playerHealthToggle.put(uuid, !enabled);
        String state = !enabled ? "enabled" : "disabled";
        player.sendMessage(ChatColor.GREEN + "Health bars " + state + ".");
    }

    private void updateEntityHealthDisplay(LivingEntity entity) {
        if (entity.isDead()) return;

        double health = entity.getHealth();
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        String entityName = getEntityName(entity);
        
        // Create health bar
        String healthBar = createHealthBar(health, maxHealth);
        Component displayName = Component.text()
            .content(entityName + " ")
            .color(NamedTextColor.WHITE)
            .append(Component.text(healthBar).color(NamedTextColor.RED))
            .build();

        entity.customName(displayName);
        entity.setCustomNameVisible(true);
    }

    private String createHealthBar(double health, double maxHealth) {
        int barLength = 20;
        int filledLength = (int) Math.round((health / maxHealth) * barLength);
        int emptyLength = barLength - filledLength;

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < filledLength; i++) bar.append("█");
        for (int i = 0; i < emptyLength; i++) bar.append("░");
        bar.append("] ").append(String.format("%.1f", health)).append("/").append(String.format("%.1f", maxHealth));

        return bar.toString();
    }

    private String getEntityName(LivingEntity entity) {
        if (entity instanceof Player) {
            return ((Player) entity).getName();
        }
        String name = entity.getType().name().replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public boolean isHealthDisplayEnabled(Player player) {
        return playerHealthToggle.getOrDefault(player.getUniqueId(), true);
    }
}
