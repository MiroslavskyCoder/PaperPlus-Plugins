package com.webx.clans.commands;

import com.webx.clans.ClansPlugin;
import com.webx.clans.models.Clan;
import com.webx.unigui.GuiService;
import com.webx.unigui.Slots;
import com.webx.unigui.Theme;
import com.webx.unigui.ThemedView;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class ClanDashboardCommand implements CommandExecutor {
    private final ClansPlugin plugin;
    private final DecimalFormat powerFormat = new DecimalFormat("#,##0.0");

    public ClanDashboardCommand(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can open the clan dashboard.");
            return true;
        }

        open(player);
        return true;
    }

    private void open(Player player) {
        GuiService gui = plugin.getGuiService();
        Clan clan = plugin.getClanManager().getClanByMember(player.getUniqueId());

        if (clan == null) {
            ThemedView view = new ThemedView("Clans", 3, Theme.light());
            view.addField(Slots.at(1, 4), "No clan", "Use /clan create to start one.");
            view.addButton(Slots.at(2, 4), "Close", ctx -> ctx.player().closeInventory());
            gui.open(player, view);
            return;
        }

        UUID uuid = player.getUniqueId();
        String rank = clan.getMemberRank(uuid);
        double power = clan.getPower();
        int members = clan.getMemberCount();
        int level = clan.getLevel();
        double progress = Math.min(1.0, level / 5.0); // Max level 5 from Clan model

        ThemedView view = new ThemedView("Clan " + clan.getTag(), 5, Theme.retroTerminal());
        view.addField(Slots.at(0, 1), "Name", clan.getName());
        view.addField(Slots.at(0, 4), "Tag", clan.getTag());
        view.addField(Slots.at(0, 7), "Rank", rank == null ? "Member" : rank);

        view.addProgress(Slots.at(1, 1), "Power", normalize(power, 0, 100_000), powerFormat.format(power));
        view.addProgress(Slots.at(1, 4), "Level", progress, "Level " + level + "/5");
        view.addField(Slots.at(1, 7), "Members", members + " online? use /clan list");

        view.addButton(Slots.at(3, 3), "Refresh", ctx -> open(ctx.player()));
        view.addButton(Slots.at(3, 5), "Close", ctx -> ctx.player().closeInventory());

        gui.open(player, view);
    }

    private double normalize(double value, double min, double max) {
        if (max <= min) return 0.0;
        double clamped = Math.max(min, Math.min(max, value));
        return (clamped - min) / (max - min);
    }
}
