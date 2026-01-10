package com.webx.shop.commands;

import com.webx.shop.ShopPlugin;
import com.webx.shop.models.Shop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class ShopCommand implements CommandExecutor {
    private final ShopPlugin plugin;

    public ShopCommand(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.openInventory(plugin.getInventoryManager().createShopListInventory());
            return true;
        }

        String shopName = args[0];
        Shop shop = plugin.getShopManager().getShop(shopName);

        if (shop == null) {
            plugin.getMessageManager().send(player, "shop-not-found",
                    Map.of("name", shopName));
            return true;
        }

        if (!shop.isEnabled()) {
            player.sendMessage("§cThis shop is disabled!");
            return true;
        }

        player.openInventory(plugin.getInventoryManager().createShopInventory(shop));
        return true;
    }
}
