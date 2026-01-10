package com.webx.regionigroks;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreateRegionListener implements Listener {
    private final RegionigroksMapPlugin plugin;

    public CreateRegionListener(RegionigroksMapPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(CreateRegionGui.TITLE)) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            ItemMeta meta = clicked.getItemMeta();
            if (meta != null && meta.getDisplayName() != null) {
                Color color = colorFromItem(clicked.getType());
                if (color != null) {
                    PendingRegion pr = plugin.getPendingRegions().computeIfAbsent(player.getUniqueId(), k -> new PendingRegion());
                    pr.setColor(color);
                    pr.setStage(PendingRegion.Stage.RADIUS_INPUT);
                    if (pr.getCenter() == null) {
                        pr.setCenter(player.getLocation());
                    }
                    player.closeInventory();
                    CreateRegionGui.openRadiusInput(player);
                    player.sendMessage(ChatColor.GREEN + "Enter radius in chat (5-256):");
                }
            }
        } else if (event.getView().getTitle().equals(CreateRegionGui.RADIUS_INPUT_TITLE)) {
            // Prevent taking the hint item
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PendingRegion pr = plugin.getPendingRegions().get(player.getUniqueId());
        if (pr == null) return;

        if (pr.getStage() == PendingRegion.Stage.RADIUS_INPUT && pr.getColor() != null) {
            event.setCancelled(true);
            String input = event.getMessage().trim();
            try {
                int radius = Integer.parseInt(input);
                if (radius < 5 || radius > 256) {
                    player.sendMessage(ChatColor.RED + "Radius must be between 5 and 256.");
                    return;
                }
                pr.setRadius(radius);
                pr.setStage(PendingRegion.Stage.NAME_INPUT);
                player.sendMessage(ChatColor.GREEN + "Radius set to " + radius + ". Enter region name:");
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid radius. Enter a number (5-256).");
            }
        } else if (pr.getStage() == PendingRegion.Stage.NAME_INPUT && pr.getColor() != null) {
            event.setCancelled(true);
            String name = event.getMessage().trim();
            if (name.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Region name cannot be empty.");
                return;
            }
            plugin.getRegionManager().createRegion(
                    name,
                    pr.getColor(),
                    player.getUniqueId(),
                    pr.getCenter() != null ? pr.getCenter() : player.getLocation(),
                    pr.getRadius()
            );
            plugin.getPendingRegions().remove(player.getUniqueId());
            plugin.saveRegions();
            player.sendMessage(ChatColor.GREEN + "Region '" + name + "' created with radius " + pr.getRadius() + ".");
        }
    }

    private Color colorFromItem(Material mat) {
        switch (mat) {
            case RED_WOOL: return Color.RED;
            case BLUE_WOOL: return Color.BLUE;
            case GREEN_WOOL: return Color.GREEN;
            case YELLOW_WOOL: return Color.YELLOW;
            case PURPLE_WOOL: return Color.PURPLE;
            case ORANGE_WOOL: return Color.ORANGE;
            default: return null;
        }
    }
}
