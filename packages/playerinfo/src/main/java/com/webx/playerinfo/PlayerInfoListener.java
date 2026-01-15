package com.webx.playerinfo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Locale;

public class PlayerInfoListener implements Listener {
    
    private final EconomyDataManager economyDataManager;
    private final SidebarManager sidebarManager;
    private final PluginIntegrationService integrationService;
    
    public PlayerInfoListener(EconomyDataManager economyDataManager, SidebarManager sidebarManager, PluginIntegrationService integrationService) {
        this.economyDataManager = economyDataManager;
        this.sidebarManager = sidebarManager;
        this.integrationService = integrationService;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        sidebarManager.ensureSidebar(player);
        player.sendMessage(
            Component.text()
                .append(Component.text("💰 PlayerInfo", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(" - Displaying your stats in action bar!", NamedTextColor.GRAY))
                .build()
        );
    }
    
    public void updateAllPlayersInfo() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerInfo(player);
        }
    }
    
    private void updatePlayerInfo(Player player) {
        // Get player stats
        String playerName = player.getName();
        int level = player.getLevel();
        double health = Math.round(player.getHealth() * 10.0) / 10.0;
        int maxHealth = (int) player.getMaxHealth();
        int food = player.getFoodLevel();
        
        EconomyDataManager.CoinSnapshot coins = economyDataManager.getCoinInfo(player);
        boolean hasCoins = coins.available();
        String walletDisplay = hasCoins ? formatCurrency(coins.wallet()) : "N/A";
        String bankDisplay = hasCoins ? formatCurrency(coins.bank()) : "N/A";
        String totalDisplay = hasCoins ? formatCurrency(coins.total()) : "N/A";
        
        // Action Bar - Updated every tick with colorful formatting
        Component actionBar = Component.text()
            .append(Component.text("💰 ", NamedTextColor.YELLOW))
            .append(Component.text(walletDisplay, NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            .append(Component.text("🏦 ", NamedTextColor.YELLOW))
            .append(Component.text(bankDisplay, NamedTextColor.GOLD))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            .append(Component.text("⭐ ", NamedTextColor.YELLOW))
            .append(Component.text("Lv" + level, NamedTextColor.AQUA, TextDecoration.BOLD))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            .append(Component.text("❤️ ", NamedTextColor.RED))
            .append(Component.text(health + "/" + maxHealth, NamedTextColor.RED))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            .append(Component.text("🍗 ", NamedTextColor.GOLD))
            .append(Component.text(food + "/20", NamedTextColor.GOLD))
            .build();
        
        player.sendActionBar(actionBar);

        PluginIntegrationService.IntegrationSnapshot integrations = integrationService.collect(player);
        
        // Sidebar update
        sidebarManager.ensureSidebar(player);
        SidebarManager.SidebarValues values = new SidebarManager.SidebarValues();
        values.playerName = playerName;
        values.level = level;
        values.coins = walletDisplay;
        values.bank = bankDisplay;
        values.total = totalDisplay;
        values.clan = formatClanInfo(integrations.clan());
        values.rank = formatRankInfo(integrations.rank());
        values.skill = formatSkillInfo(integrations.skill());
        values.quest = formatQuestInfo(integrations.quest());
        values.job = formatJobInfo(integrations.job());
        values.market = formatMarketInfo(integrations.market());
        values.marketplace = formatMarketplaceInfo(integrations.marketplace());
        values.feed = formatFeedInfo(integrations.feed(), food);
        sidebarManager.updateSidebar(player, values);
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.US, "$%,.2f", amount);
    }

    private String formatClanInfo(PluginIntegrationService.ClanInfo clan) {
        if (clan == null || !clan.available()) {
            return "—";
        }
        StringBuilder builder = new StringBuilder();
        String tag = safe(clan.tag());
        String name = safe(clan.name());
        String role = safe(clan.role());
        if (!tag.isBlank()) {
            builder.append("[").append(tag).append("] ");
        }
        if (!name.isBlank()) {
            builder.append(name);
        } else {
            builder.append("Clan");
        }
        if (!role.isBlank()) {
            builder.append(" (").append(role).append(")");
        }
        if (clan.power() > 0) {
            builder.append(" • ").append((int) clan.power()).append(" pow");
        }
        return builder.toString();
    }

    private String formatRankInfo(PluginIntegrationService.RankInfo rank) {
        if (rank == null || !rank.available()) {
            return "—";
        }
        String name = safe(rank.displayName());
        if (name.isBlank()) {
            name = "Default";
        }
        if (!rank.active()) {
            return name + " (paused)";
        }
        if (rank.expiresAt() > 0) {
            return name + " ⏳";
        }
        return name;
    }

    private String formatSkillInfo(PluginIntegrationService.SkillInfo skill) {
        if (skill == null || !skill.available()) {
            return "—";
        }
        String topSkill = safe(skill.topSkill());
        if (topSkill.isBlank()) {
            topSkill = "Skill";
        }
        return topSkill + " " + skill.value();
    }

    private String formatQuestInfo(PluginIntegrationService.QuestInfo quest) {
        if (quest == null || !quest.available()) {
            return "—";
        }
        String name = safe(quest.questName());
        if (name.isBlank()) {
            name = "Active quest";
        }
        return name + " " + quest.percentComplete() + "%";
    }

    private String formatJobInfo(PluginIntegrationService.JobInfo job) {
        if (job == null || !job.available()) {
            return "—";
        }
        String jobName = safe(job.jobName());
        if (jobName.isBlank()) {
            jobName = "Job";
        }
        return jobName + " Lv" + job.level();
    }

    private String formatMarketInfo(PluginIntegrationService.MarketInfo market) {
        if (market == null || !market.available()) {
            return "—";
        }
        String highlight = safe(market.highlight());
        if (!highlight.isBlank()) {
            return highlight + " " + formatCurrency(market.highlightPrice());
        }
        return "Tracked: " + market.trackedItems();
    }

    private String formatMarketplaceInfo(PluginIntegrationService.MarketplaceInfo info) {
        if (info == null || !info.available()) {
            return "—";
        }
        return info.playerListings() + "/" + info.totalListings();
    }

    private String formatFeedInfo(PluginIntegrationService.FeedInfo info, int vanillaFoodLevel) {
        if (info == null || !info.available()) {
            return vanillaFoodLevel + "/20";
        }
        return info.hunger() + "/20 (+" + info.hungerRestore() + ")";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
