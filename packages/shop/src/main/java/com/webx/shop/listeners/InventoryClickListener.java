package com.webx.shop.listeners;

import com.webx.shop.ShopPlugin;
import com.webx.shop.gui.ShopGUI;
import com.webx.shop.models.ShopItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryClickListener implements Listener {
    private final ShopPlugin plugin;
    private final Map<Inventory, ShopGUI> openGUIs;

    public InventoryClickListener(ShopPlugin plugin) {
        this.plugin = plugin;
        this.openGUIs = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Component titleComponent = event.getView().title();
        String title = PlainTextComponentSerializer.plainText().serialize(titleComponent);
        
        if (!title.equals("Shop")) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();

        // Handle close button
        if (slot == 53 && clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // Handle info item
        if (slot == 49 && clickedItem.getType() == Material.PAPER) {
            return;
        }

        // Handle shop item purchase
        if (slot < 45) {
            ShopGUI shopGUI = new ShopGUI(plugin, plugin.getShopManager().getShopItems());
            ShopItem shopItem = shopGUI.getShopItem(slot);
            
            if (shopItem != null) {
                if (shopGUI.purchaseItem(player, shopItem)) {
                    // Reopen GUI to show updated balance
                    player.closeInventory();
                    shopGUI.open(player);
                }
            }
        }
    }
}
