package com.webx.shop.commands;

import com.webx.shop.ShopPlugin;
import com.webx.shop.managers.ShopManager;
import com.webx.shop.services.ShopSyncService;
import com.webx.unigui.GuiService;
import com.webx.unigui.Slots;
import com.webx.unigui.Theme;
import com.webx.unigui.ThemedView;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ShopDashboardCommand implements CommandExecutor {
    private final ShopPlugin plugin;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.US).withZone(ZoneId.systemDefault());
    private final DecimalFormat sizeFormat = new DecimalFormat("#,##0.0");

    public ShopDashboardCommand(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can open the shop dashboard.");
            return true;
        }

        if (!player.hasPermission("shop.admin")) {
            player.sendMessage(ChatColor.RED + "You need shop.admin to open this dashboard.");
            return true;
        }

        open(player);
        return true;
    }

    private void open(Player player) {
        GuiService gui = plugin.getGuiService();
        ShopManager manager = plugin.getShopManager();
        ShopSyncService sync = plugin.getShopSyncService();

        int itemCount = manager.getShopItems().size();
        Path shopPath = plugin.getDataFolder().toPath().resolve("shop.json");

        long lastModified = readLastModified(shopPath);
        long sizeBytes = readSize(shopPath);
        boolean syncEnabled = plugin.getConfig().getBoolean("sync.enabled", true);
        long interval = plugin.getConfig().getLong("sync.interval-ticks", 200L);

        ThemedView view = new ThemedView("Shop Dashboard", 5, Theme.glass());
        view.addField(Slots.at(0, 1), "Items", String.valueOf(itemCount));
        view.addField(Slots.at(0, 4), "shop.json", humanSize(sizeBytes));
        view.addField(Slots.at(0, 7), "Modified", lastModified == 0 ? "n/a" : fmt.format(Instant.ofEpochMilli(lastModified)));
        view.addProgress(Slots.at(1, 1), "Sync", syncEnabled ? 1.0 : 0.0, "Interval: " + interval + "t");

        view.addButton(Slots.at(3, 3), "Reload Items", ctx -> {
            int count = manager.reloadShopItems();
            ctx.player().sendMessage(ChatColor.GREEN + "Reloaded " + count + " shop items from disk.");
            open(ctx.player());
        });

        view.addButton(Slots.at(3, 5), "Restart Watcher", ctx -> {
            sync.stop();
            sync.start();
            ctx.player().sendMessage(ChatColor.GREEN + "Shop sync watcher restarted.");
            open(ctx.player());
        });

        view.addButton(Slots.at(4, 4), "Refresh", ctx -> open(ctx.player()));

        gui.open(player, view);
    }

    private long readLastModified(Path path) {
        try {
            if (Files.exists(path)) {
                return Files.getLastModifiedTime(path).toMillis();
            }
        } catch (Exception ignored) { }
        return 0L;
    }

    private long readSize(Path path) {
        try {
            if (Files.exists(path)) {
                return Files.size(path);
            }
        } catch (Exception ignored) { }
        return 0L;
    }

    private String humanSize(long bytes) {
        if (bytes <= 0) return "0 KB";
        double kb = bytes / 1024.0;
        if (kb < 1024) return sizeFormat.format(kb) + " KB";
        double mb = kb / 1024.0;
        return sizeFormat.format(mb) + " MB";
    }
}
