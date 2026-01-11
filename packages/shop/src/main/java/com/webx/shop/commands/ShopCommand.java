package com.webx.shop.commands;

import com.webx.shop.ShopPlugin;
import com.webx.shop.gui.ShopGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {
    private final ShopPlugin plugin;

    public ShopCommand(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED));
            return true;
        }

        if (plugin.getShopManager().getShopItems().isEmpty()) {
            player.sendMessage(Component.text("Shop is currently empty!", NamedTextColor.RED));
            return true;
        }

        ShopGUI shopGUI = new ShopGUI(plugin, plugin.getShopManager().getShopItems());
        shopGUI.open(player);
        
        player.sendMessage(Component.text("Opening shop...", NamedTextColor.GREEN));
        return true;
    }
}
