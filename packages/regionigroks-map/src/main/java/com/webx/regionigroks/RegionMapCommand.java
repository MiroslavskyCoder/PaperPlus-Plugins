package com.webx.regionigroks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class RegionMapCommand implements CommandExecutor {
    private final RegionigroksMapPlugin plugin;

    public RegionMapCommand(RegionigroksMapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        Player player = (Player) sender;
        World world = player.getWorld();

        MapView view = Bukkit.createMap(world);
        // Clean default renderers and add our minimap renderer
        view.getRenderers().forEach(view::removeRenderer);
        view.addRenderer(new MinimapRenderer(plugin));
        view.setScale(MapView.Scale.CLOSE);
        view.setUnlimitedTracking(true);
        view.setTrackingPosition(true);

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP, 1);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();
        if (meta != null) {
            meta.setMapView(view);
            meta.setDisplayName("Minimap");
            mapItem.setItemMeta(meta);
        }

        // Give to offhand if empty, else main hand
        if (player.getInventory().getItemInOffHand().getType() == Material.AIR) {
            player.getInventory().setItemInOffHand(mapItem);
        } else {
            player.getInventory().addItem(mapItem);
        }
        player.sendMessage("Minimap given. Hold the map to see live view.");
        return true;
    }
}
