package com.webx.shop.commands;

import com.webx.shop.ShopPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminShopCommand implements CommandExecutor {
    private final ShopPlugin plugin;

    public AdminShopCommand(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (!player.hasPermission("shop.admin")) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /adminshop <create|delete|edit|list> [name]");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /adminshop create <name>");
                    return true;
                }
                String shopName = args[1];
                var shop = plugin.getShopManager().createShop(shopName, player.getUniqueId());
                if (shop != null) {
                    plugin.getMessageManager().send(player, "shop-created",
                            java.util.Map.of("name", shopName));
                } else {
                    player.sendMessage("§cShop already exists!");
                }
            }
            case "delete" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /adminshop delete <name>");
                    return true;
                }
                String shopName = args[1];
                plugin.getShopManager().deleteShop(shopName);
                plugin.getMessageManager().send(player, "shop-deleted",
                        java.util.Map.of("name", shopName));
            }
            case "list" -> {
                player.sendMessage("§6§lShops:");
                plugin.getShopManager().getAllShops().forEach(shop -> {
                    player.sendMessage("§e  - §6" + shop.getName() + " §7(" + shop.getItemCount() + " items)");
                });
            }
            default -> player.sendMessage("§cUsage: /adminshop <create|delete|edit|list> [name]");
        }

        return true;
    }
}
