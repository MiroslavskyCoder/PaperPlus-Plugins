package com.webx.playerinfo;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Aggregates per-player insights from other plugins via reflection or shared data cache.
 */
public class PluginIntegrationService {

    private static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.0");

    private final JavaPlugin plugin;
    private final SharedDataCache sharedDataCache;
    private final ClanIntegration clanIntegration;
    private final RankIntegration rankIntegration;
    private final SkillsIntegration skillsIntegration;
    private final QuestIntegration questIntegration;
    private final JobIntegration jobIntegration;
    private final MarketIntegration marketIntegration;
    private final MarketplaceIntegration marketplaceIntegration;
    private final FeedIntegration feedIntegration;

    public PluginIntegrationService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.sharedDataCache = new SharedDataCache(plugin);
        this.clanIntegration = new ClanIntegration(plugin);
        this.rankIntegration = new RankIntegration(plugin);
        this.skillsIntegration = new SkillsIntegration(plugin, sharedDataCache);
        this.questIntegration = new QuestIntegration(plugin);
        this.jobIntegration = new JobIntegration(plugin, sharedDataCache);
        this.marketIntegration = new MarketIntegration(sharedDataCache);
        this.marketplaceIntegration = new MarketplaceIntegration(plugin);
        this.feedIntegration = new FeedIntegration(plugin);
    }

    public void refreshCaches() {
        sharedDataCache.refresh();
    }

    public IntegrationSnapshot collect(Player player) {
        return new IntegrationSnapshot(
            clanIntegration.fetch(player),
            rankIntegration.fetch(player),
            skillsIntegration.fetch(player),
            questIntegration.fetch(player),
            jobIntegration.fetch(player),
            marketIntegration.fetch(),
            marketplaceIntegration.fetch(player),
            feedIntegration.fetch(player)
        );
    }

    public record IntegrationSnapshot(
            ClanInfo clan,
            RankInfo rank,
            SkillInfo skill,
            QuestInfo quest,
            JobInfo job,
            MarketInfo market,
            MarketplaceInfo marketplace,
            FeedInfo feed) {}

    public record ClanInfo(boolean available, String tag, String name, String role, double power, int members) {
        public static ClanInfo unavailable() {
            return new ClanInfo(false, "", "", "", 0D, 0);
        }
    }

    public record RankInfo(boolean available, String displayName, String prefix, long expiresAt, boolean active) {
        public static RankInfo unavailable() {
            return new RankInfo(false, "", "", 0L, false);
        }
    }

    public record SkillInfo(boolean available, String topSkill, int value) {
        public static SkillInfo unavailable() {
            return new SkillInfo(false, "", 0);
        }
    }

    public record QuestInfo(boolean available, String questName, String status, int percentComplete) {
        public static QuestInfo unavailable() {
            return new QuestInfo(false, "", "", 0);
        }
    }

    public record JobInfo(boolean available, String jobName, int level) {
        public static JobInfo unavailable() {
            return new JobInfo(false, "", 0);
        }
    }

    public record MarketInfo(boolean available, int trackedItems, String highlight, double highlightPrice) {
        public static MarketInfo unavailable() {
            return new MarketInfo(false, 0, "", 0D);
        }
    }

    public record MarketplaceInfo(boolean available, int playerListings, int totalListings) {
        public static MarketplaceInfo unavailable() {
            return new MarketplaceInfo(false, 0, 0);
        }
    }

    public record FeedInfo(boolean available, int hunger, int saturation, int hungerRestore) {
        public static FeedInfo unavailable() {
            return new FeedInfo(false, 0, 0, 0);
        }
    }

    private static String formatDouble(double value) {
        synchronized (ONE_DECIMAL) {
            return ONE_DECIMAL.format(value);
        }
    }

    // ===== CLANS =====
    private static final class ClanIntegration {
        private final JavaPlugin plugin;
        private Plugin clansPlugin;
        private Method getClanManagerMethod;
        private Method getClanByMemberMethod;
        private Method getClanNameMethod;
        private Method getClanTagMethod;
        private Method getMemberRankMethod;
        private Method getPowerMethod;
        private Method getMemberCountMethod;
        private boolean loggedMissing;
        private boolean loggedError;

        private ClanIntegration(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        ClanInfo fetch(Player player) {
            if (!ensureHook()) {
                return ClanInfo.unavailable();
            }
            try {
                Object clanManager = getClanManagerMethod.invoke(clansPlugin);
                if (clanManager == null) {
                    return ClanInfo.unavailable();
                }
                Object clan = getClanByMemberMethod.invoke(clanManager, player.getUniqueId());
                if (clan == null) {
                    return ClanInfo.unavailable();
                }
                String name = (String) getClanNameMethod.invoke(clan);
                String tag = (String) getClanTagMethod.invoke(clan);
                String role = (String) getMemberRankMethod.invoke(clan, player.getUniqueId());
                Number power = (Number) getPowerMethod.invoke(clan);
                Number members = (Number) getMemberCountMethod.invoke(clan);
                return new ClanInfo(true,
                        defaultString(tag),
                        defaultString(name),
                        defaultString(role),
                        power != null ? power.doubleValue() : 0D,
                        members != null ? members.intValue() : 0);
            } catch (Exception ex) {
                if (!loggedError) {
                    plugin.getLogger().warning("PlayerInfo: Failed to read clan data: " + ex.getMessage());
                    loggedError = true;
                }
                return ClanInfo.unavailable();
            }
        }

        private boolean ensureHook() {
            if (clansPlugin != null && clansPlugin.isEnabled()) {
                return true;
            }
            Plugin detected = Bukkit.getPluginManager().getPlugin("Clans");
            if (detected == null || !detected.isEnabled()) {
                if (!loggedMissing) {
                    plugin.getLogger().info("PlayerInfo: Clans plugin not loaded - clan section hidden.");
                    loggedMissing = true;
                }
                clansPlugin = null;
                return false;
            }
            try {
                Class<?> pluginClass = Class.forName("com.webx.clans.ClansPlugin");
                if (!pluginClass.isInstance(detected)) {
                    plugin.getLogger().warning("PlayerInfo: Clans plugin class mismatch, skipping integration.");
                    clansPlugin = null;
                    return false;
                }
                Class<?> clanManagerClass = Class.forName("com.webx.clans.managers.ClanManager");
                Class<?> clanClass = Class.forName("com.webx.clans.models.Clan");
                getClanManagerMethod = pluginClass.getMethod("getClanManager");
                getClanByMemberMethod = clanManagerClass.getMethod("getClanByMember", UUID.class);
                getClanNameMethod = clanClass.getMethod("getName");
                getClanTagMethod = clanClass.getMethod("getTag");
                getMemberRankMethod = clanClass.getMethod("getMemberRank", UUID.class);
                getPowerMethod = clanClass.getMethod("getPower");
                getMemberCountMethod = clanClass.getMethod("getMemberCount");
                clansPlugin = detected;
                loggedMissing = false;
                loggedError = false;
                plugin.getLogger().info("PlayerInfo: Clans integration enabled.");
                return true;
            } catch (Exception ex) {
                plugin.getLogger().warning("PlayerInfo: Unable to hook Clans plugin: " + ex.getMessage());
                clansPlugin = null;
                return false;
            }
        }
    }

    // ===== RANKS =====
    private static final class RankIntegration {
        private final JavaPlugin plugin;
        private Plugin ranksPlugin;
        private Method getPlayerRankManagerMethod;
        private Method getRankManagerMethod;
        private Method getPlayerRankMethod;
        private Method getPrimaryRankMethod;
        private Method getExpiresAtMethod;
        private Method isActiveMethod;
        private Method getRankMethod;
        private Method getDisplayNameMethod;
        private Method getPrefixMethod;
        private boolean loggedMissing;
        private boolean loggedError;

        private RankIntegration(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        RankInfo fetch(Player player) {
            if (!ensureHook()) {
                return RankInfo.unavailable();
            }
            try {
                Object playerRankManager = getPlayerRankManagerMethod.invoke(ranksPlugin);
                Object rankManager = getRankManagerMethod.invoke(ranksPlugin);
                if (playerRankManager == null || rankManager == null) {
                    return RankInfo.unavailable();
                }
                Object playerRank = getPlayerRankMethod.invoke(playerRankManager, player.getUniqueId());
                String rankId = playerRank != null ? (String) getPrimaryRankMethod.invoke(playerRank) : "member";
                if (rankId == null || rankId.isBlank()) {
                    rankId = "member";
                }
                long expiresAt = 0;
                boolean active = true;
                if (playerRank != null) {
                    Number expires = (Number) getExpiresAtMethod.invoke(playerRank);
                    expiresAt = expires != null ? expires.longValue() : 0L;
                    Object activeFlag = isActiveMethod.invoke(playerRank);
                    if (activeFlag instanceof Boolean b) {
                        active = b;
                    }
                }
                Object rank = getRankMethod.invoke(rankManager, rankId);
                String display = rank != null ? (String) getDisplayNameMethod.invoke(rank) : rankId;
                String prefix = rank != null ? (String) getPrefixMethod.invoke(rank) : "";
                return new RankInfo(true, defaultString(display), defaultString(prefix), expiresAt, active);
            } catch (Exception ex) {
                if (!loggedError) {
                    plugin.getLogger().warning("PlayerInfo: Failed to read rank data: " + ex.getMessage());
                    loggedError = true;
                }
                return RankInfo.unavailable();
            }
        }

        private boolean ensureHook() {
            if (ranksPlugin != null && ranksPlugin.isEnabled()) {
                return true;
            }
            Plugin detected = Bukkit.getPluginManager().getPlugin("Ranks");
            if (detected == null || !detected.isEnabled()) {
                if (!loggedMissing) {
                    plugin.getLogger().info("PlayerInfo: Ranks plugin not loaded - rank section hidden.");
                    loggedMissing = true;
                }
                ranksPlugin = null;
                return false;
            }
            try {
                Class<?> pluginClass = Class.forName("com.webx.ranks.RanksPlugin");
                if (!pluginClass.isInstance(detected)) {
                    plugin.getLogger().warning("PlayerInfo: Ranks plugin class mismatch, skipping integration.");
                    ranksPlugin = null;
                    return false;
                }
                Class<?> playerRankManagerClass = Class.forName("com.webx.ranks.managers.PlayerRankManager");
                Class<?> playerRankClass = Class.forName("com.webx.ranks.models.PlayerRank");
                Class<?> rankManagerClass = Class.forName("com.webx.ranks.managers.RankManager");
                Class<?> rankClass = Class.forName("com.webx.ranks.models.Rank");
                getPlayerRankManagerMethod = pluginClass.getMethod("getPlayerRankManager");
                getRankManagerMethod = pluginClass.getMethod("getRankManager");
                getPlayerRankMethod = playerRankManagerClass.getMethod("getPlayerRank", UUID.class);
                getPrimaryRankMethod = playerRankClass.getMethod("getPrimaryRank");
                getExpiresAtMethod = playerRankClass.getMethod("getExpiresAt");
                isActiveMethod = playerRankClass.getMethod("isActive");
                getRankMethod = rankManagerClass.getMethod("getRank", String.class);
                getDisplayNameMethod = rankClass.getMethod("getDisplayName");
                getPrefixMethod = rankClass.getMethod("getPrefix");
                ranksPlugin = detected;
                loggedMissing = false;
                loggedError = false;
                plugin.getLogger().info("PlayerInfo: Ranks integration enabled.");
                return true;
            } catch (Exception ex) {
                plugin.getLogger().warning("PlayerInfo: Unable to hook Ranks plugin: " + ex.getMessage());
                ranksPlugin = null;
                return false;
            }
        }
    }

    // ===== SKILLS =====
    private static final class SkillsIntegration {
        private final JavaPlugin plugin;
        private final SharedDataCache sharedDataCache;
        private Plugin skillsPlugin;
        private Method getSkillsManagerMethod;
        private Field skillsManagerField;
        private Method getSkillsMethod;
        private boolean loggedMissing;
        private boolean loggedError;

        private SkillsIntegration(JavaPlugin plugin, SharedDataCache sharedDataCache) {
            this.plugin = plugin;
            this.sharedDataCache = sharedDataCache;
        }

        SkillInfo fetch(Player player) {
            Map<String, Integer> skills = fetchFromPlugin(player);
            if (skills.isEmpty()) {
                JsonObject skillsNode = sharedDataCache.getPlayerChild(player.getUniqueId(), "skills");
                if (skillsNode != null) {
                    for (String key : skillsNode.keySet()) {
                        JsonElement element = skillsNode.get(key);
                        if (element != null && element.isJsonPrimitive()) {
                            skills.put(key, element.getAsInt());
                        }
                    }
                }
            }

            if (skills.isEmpty()) {
                // Fallback to vanilla XP as a pseudo skill metric
                int vanilla = player.getTotalExperience();
                return new SkillInfo(false, "Experience", vanilla);
            }

            Optional<Map.Entry<String, Integer>> top = skills.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue));
            if (top.isEmpty()) {
                return SkillInfo.unavailable();
            }
            Map.Entry<String, Integer> entry = top.get();
            return new SkillInfo(true, entry.getKey(), entry.getValue());
        }

        @SuppressWarnings("unchecked")
        private Map<String, Integer> fetchFromPlugin(Player player) {
            if (!ensureHook()) {
                return new HashMap<>();
            }
            try {
                Object manager = resolveManager();
                if (manager == null) {
                    return new HashMap<>();
                }
                if (getSkillsMethod == null) {
                    getSkillsMethod = manager.getClass().getMethod("getSkills", Player.class);
                }
                Object result = getSkillsMethod.invoke(manager, player);
                if (result instanceof Map<?, ?> map) {
                    Map<String, Integer> converted = new HashMap<>();
                    map.forEach((k, v) -> {
                        if (k instanceof String key && v instanceof Number number) {
                            converted.put(key, number.intValue());
                        }
                    });
                    return converted;
                }
            } catch (Exception ex) {
                if (!loggedError) {
                    plugin.getLogger().warning("PlayerInfo: Failed to read Skills data: " + ex.getMessage());
                    loggedError = true;
                }
            }
            return new HashMap<>();
        }

        private Object resolveManager() {
            try {
                if (skillsPlugin == null) {
                    return null;
                }
                if (getSkillsManagerMethod != null) {
                    return getSkillsManagerMethod.invoke(skillsPlugin);
                }
                if (skillsManagerField != null) {
                    return skillsManagerField.get(skillsPlugin);
                }
            } catch (Exception ex) {
                if (!loggedError) {
                    plugin.getLogger().warning("PlayerInfo: Unable to access Skills manager: " + ex.getMessage());
                    loggedError = true;
                }
            }
            return null;
        }

        private boolean ensureHook() {
            if (skillsPlugin != null && skillsPlugin.isEnabled()) {
                return true;
            }
            Plugin detected = Bukkit.getPluginManager().getPlugin("Skills");
            if (detected == null || !detected.isEnabled()) {
                if (!loggedMissing) {
                    plugin.getLogger().info("PlayerInfo: Skills plugin not loaded - skills section falling back to vanilla XP.");
                    loggedMissing = true;
                }
                skillsPlugin = null;
                return false;
            }
            try {
                Class<?> pluginClass = Class.forName("com.webx.skills.SkillsPlugin");
                if (!pluginClass.isInstance(detected)) {
                    plugin.getLogger().warning("PlayerInfo: Skills plugin class mismatch, skipping reflection hook.");
                    skillsPlugin = null;
                    return false;
                }
                skillsPlugin = detected;
                // Try direct getter first
                try {
                    getSkillsManagerMethod = pluginClass.getMethod("getSkillsManager");
                } catch (NoSuchMethodException ignored) {
                    getSkillsManagerMethod = null;
                }
                if (getSkillsManagerMethod == null) {
                    // Attempt to find field of type SkillsManager
                    for (Field field : pluginClass.getDeclaredFields()) {
                        if (field.getType().getName().equals("com.webx.skills.SkillsManager")) {
                            field.setAccessible(true);
                            skillsManagerField = field;
                            break;
                        }
                    }
                }
                loggedMissing = false;
                loggedError = false;
                if (getSkillsManagerMethod != null || skillsManagerField != null) {
                    plugin.getLogger().info("PlayerInfo: Skills integration enabled.");
                } else {
                    plugin.getLogger().warning("PlayerInfo: Skills plugin found but no manager accessor detected.");
                }
                return getSkillsManagerMethod != null || skillsManagerField != null;
            } catch (ClassNotFoundException ex) {
                plugin.getLogger().warning("PlayerInfo: Unable to resolve Skills classes: " + ex.getMessage());
                skillsPlugin = null;
                return false;
            }
        }
    }

    // ===== QUESTS =====
    private static final class QuestIntegration {
        private final JavaPlugin plugin;
        private Plugin questsPlugin;
        private Method getQuestManagerMethod;
        private Method getProgressManagerMethod;
        private Method getActiveQuestsMethod;
        private Method questProgressGetQuestIdMethod;
        private Method questProgressGetStatusMethod;
        private Method questProgressIsCompletedMethod;
        private Method questProgressGetObjectiveProgressMethod;
        private Method questManagerGetQuestMethod;
        private Method questGetNameMethod;
        private Method questGetObjectivesMethod;
        private Method objectiveGetIdMethod;
        private Method objectiveGetRequiredMethod;
        private boolean loggedMissing;
        private boolean loggedError;

        private QuestIntegration(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        QuestInfo fetch(Player player) {
            if (!ensureHook()) {
                return QuestInfo.unavailable();
            }
            try {
                Object progressManager = getProgressManagerMethod.invoke(questsPlugin);
                Object questManager = getQuestManagerMethod.invoke(questsPlugin);
                if (progressManager == null || questManager == null) {
                    return QuestInfo.unavailable();
                }
                @SuppressWarnings("unchecked")
                Collection<Object> active = (Collection<Object>) getActiveQuestsMethod.invoke(progressManager, player.getUniqueId());
                if (active == null || active.isEmpty()) {
                    return QuestInfo.unavailable();
                }
                Object targetProgress = active.stream()
                        .filter(progress -> !isCompleted(progress))
                        .findFirst()
                        .orElse(active.iterator().next());

                String questId = (String) questProgressGetQuestIdMethod.invoke(targetProgress);
                String status = String.valueOf(questProgressGetStatusMethod.invoke(targetProgress));
                Object quest = questManagerGetQuestMethod.invoke(questManager, questId);
                String questName = quest != null ? (String) questGetNameMethod.invoke(quest) : questId;

                int percent = 0;
                if (quest != null) {
                    @SuppressWarnings("unchecked")
                    List<Object> objectives = (List<Object>) questGetObjectivesMethod.invoke(quest);
                    if (objectives != null && !objectives.isEmpty()) {
                        Object objective = objectives.get(0);
                        String objectiveId = (String) objectiveGetIdMethod.invoke(objective);
                        Number required = (Number) objectiveGetRequiredMethod.invoke(objective);
                        Number current = (Number) questProgressGetObjectiveProgressMethod.invoke(targetProgress, objectiveId);
                        if (required != null && required.intValue() > 0 && current != null) {
                            percent = (int) Math.min(100,
                                    Math.round((current.doubleValue() / required.doubleValue()) * 100D));
                        }
                    }
                }

                return new QuestInfo(true, questName, status, percent);
            } catch (Exception ex) {
                if (!loggedError) {
                    plugin.getLogger().warning("PlayerInfo: Failed to read quest data: " + ex.getMessage());
                    loggedError = true;
                }
                return QuestInfo.unavailable();
            }
        }

        private boolean isCompleted(Object progress) {
            try {
                Object result = questProgressIsCompletedMethod.invoke(progress);
                return result instanceof Boolean b && b;
            } catch (Exception ignored) {
                return false;
            }
        }

        private boolean ensureHook() {
            if (questsPlugin != null && questsPlugin.isEnabled()) {
                return true;
            }
            Plugin detected = Bukkit.getPluginManager().getPlugin("Quests");
            if (detected == null || !detected.isEnabled()) {
                if (!loggedMissing) {
                    plugin.getLogger().info("PlayerInfo: Quests plugin not loaded - quest section hidden.");
                    loggedMissing = true;
                }
                questsPlugin = null;
                return false;
            }
            try {
                Class<?> pluginClass = Class.forName("com.webx.quests.QuestsPlugin");
                if (!pluginClass.isInstance(detected)) {
                    plugin.getLogger().warning("PlayerInfo: Quests plugin class mismatch, skipping integration.");
                    questsPlugin = null;
                    return false;
                }
                Class<?> questManagerClass = Class.forName("com.webx.quests.managers.QuestManager");
                Class<?> progressManagerClass = Class.forName("com.webx.quests.managers.ProgressManager");
                Class<?> questProgressClass = Class.forName("com.webx.quests.models.QuestProgress");
                Class<?> questClass = Class.forName("com.webx.quests.models.Quest");
                Class<?> objectiveClass = Class.forName("com.webx.quests.models.QuestObjective");

                getQuestManagerMethod = pluginClass.getMethod("getQuestManager");
                getProgressManagerMethod = pluginClass.getMethod("getProgressManager");
                getActiveQuestsMethod = progressManagerClass.getMethod("getActiveQuests", UUID.class);
                questProgressGetQuestIdMethod = questProgressClass.getMethod("getQuestId");
                questProgressGetStatusMethod = questProgressClass.getMethod("getStatus");
                questProgressIsCompletedMethod = questProgressClass.getMethod("isCompleted");
                questProgressGetObjectiveProgressMethod = questProgressClass.getMethod("getObjectiveProgress", String.class);
                questManagerGetQuestMethod = questManagerClass.getMethod("getQuest", String.class);
                questGetNameMethod = questClass.getMethod("getName");
                questGetObjectivesMethod = questClass.getMethod("getObjectives");
                objectiveGetIdMethod = objectiveClass.getMethod("getId");
                objectiveGetRequiredMethod = objectiveClass.getMethod("getRequired");

                questsPlugin = detected;
                loggedMissing = false;
                loggedError = false;
                plugin.getLogger().info("PlayerInfo: Quests integration enabled.");
                return true;
            } catch (Exception ex) {
                plugin.getLogger().warning("PlayerInfo: Unable to hook Quests plugin: " + ex.getMessage());
                questsPlugin = null;
                return false;
            }
        }
    }

    // ===== JOBS =====
    private static final class JobIntegration {
        private final JavaPlugin plugin;
        private final SharedDataCache sharedDataCache;
        private Plugin jobsPlugin;
        private Method getJobManagerMethod;
        private Field jobManagerField;
        private Method getJobMethod;
        private Method getJobLevelMethod;
        private boolean loggedMissing;
        private boolean loggedError;

        private JobIntegration(JavaPlugin plugin, SharedDataCache sharedDataCache) {
            this.plugin = plugin;
            this.sharedDataCache = sharedDataCache;
        }

        JobInfo fetch(Player player) {
            if (ensureHook()) {
                try {
                    Object manager = resolveManager();
                    if (manager != null) {
                        if (getJobMethod == null) {
                            getJobMethod = manager.getClass().getMethod("getJob", Player.class);
                        }
                        if (getJobLevelMethod == null) {
                            getJobLevelMethod = manager.getClass().getMethod("getJobLevel", Player.class);
                        }
                        String jobName = (String) getJobMethod.invoke(manager, player);
                        Number level = (Number) getJobLevelMethod.invoke(manager, player);
                        if (jobName != null) {
                            return new JobInfo(true, jobName, level != null ? level.intValue() : 1);
                        }
                    }
                } catch (Exception ex) {
                    if (!loggedError) {
                        plugin.getLogger().warning("PlayerInfo: Failed to read Jobs data: " + ex.getMessage());
                        loggedError = true;
                    }
                }
            }

            JsonObject jobNode = sharedDataCache.getPlayerChild(player.getUniqueId(), "jobs");
            if (jobNode != null) {
                String name = jobNode.has("name") ? jobNode.get("name").getAsString() : "";
                int level = jobNode.has("level") ? jobNode.get("level").getAsInt() : 1;
                if (!name.isBlank()) {
                    return new JobInfo(true, name, level);
                }
            }

            return JobInfo.unavailable();
        }

        private Object resolveManager() throws IllegalAccessException, java.lang.reflect.InvocationTargetException {
            if (jobsPlugin == null) {
                return null;
            }
            if (getJobManagerMethod != null) {
                return getJobManagerMethod.invoke(jobsPlugin);
            }
            if (jobManagerField != null) {
                return jobManagerField.get(jobsPlugin);
            }
            return null;
        }

        private boolean ensureHook() {
            if (jobsPlugin != null && jobsPlugin.isEnabled()) {
                return true;
            }
            Plugin detected = Bukkit.getPluginManager().getPlugin("Jobs");
            if (detected == null || !detected.isEnabled()) {
                if (!loggedMissing) {
                    plugin.getLogger().info("PlayerInfo: Jobs plugin not loaded - reading jobs info from shared data only.");
                    loggedMissing = true;
                }
                jobsPlugin = null;
                return false;
            }
            try {
                Class<?> pluginClass = Class.forName("com.webx.jobs.JobsPlugin");
                if (!pluginClass.isInstance(detected)) {
                    plugin.getLogger().warning("PlayerInfo: Jobs plugin class mismatch, skipping reflection hook.");
                    jobsPlugin = null;
                    return false;
                }
                try {
                    getJobManagerMethod = pluginClass.getMethod("getJobManager");
                } catch (NoSuchMethodException ignored) {
                    getJobManagerMethod = null;
                }
                if (getJobManagerMethod == null) {
                    for (Field field : pluginClass.getDeclaredFields()) {
                        if (field.getType().getName().equals("com.webx.jobs.JobManager")) {
                            field.setAccessible(true);
                            jobManagerField = field;
                            break;
                        }
                    }
                }
                jobsPlugin = detected;
                loggedMissing = false;
                loggedError = false;
                if (getJobManagerMethod != null || jobManagerField != null) {
                    plugin.getLogger().info("PlayerInfo: Jobs integration enabled.");
                } else {
                    plugin.getLogger().warning("PlayerInfo: Jobs plugin exposes no manager - relying on shared data.");
                }
                return getJobManagerMethod != null || jobManagerField != null;
            } catch (ClassNotFoundException ex) {
                plugin.getLogger().warning("PlayerInfo: Unable to resolve Jobs classes: " + ex.getMessage());
                jobsPlugin = null;
                return false;
            }
        }
    }

    // ===== MARKET =====
    private static final class MarketIntegration {
        private final SharedDataCache sharedDataCache;

        private MarketIntegration(SharedDataCache sharedDataCache) {
            this.sharedDataCache = sharedDataCache;
        }

        MarketInfo fetch() {
            JsonObject marketNode = sharedDataCache.getPluginSection("Market");
            if (marketNode == null) {
                return MarketInfo.unavailable();
            }
            int tracked = 0;
            if (marketNode.has("items") && marketNode.get("items").isJsonArray()) {
                tracked = marketNode.getAsJsonArray("items").size();
            } else if (marketNode.has("trackedItems")) {
                tracked = marketNode.get("trackedItems").getAsInt();
            }
            String highlight = marketNode.has("trendingItem") ? marketNode.get("trendingItem").getAsString() : "";
            double price = marketNode.has("trendingPrice") ? marketNode.get("trendingPrice").getAsDouble() : 0D;
            return new MarketInfo(tracked > 0, tracked, highlight, price);
        }
    }

    // ===== MARKETPLACE =====
    private static final class MarketplaceIntegration {
        private final JavaPlugin plugin;
        private Plugin marketplacePlugin;
        private Method getMarketplaceManagerMethod;
        private Method getListingsMethod;
        private Method shopItemGetSellerMethod;
        private boolean loggedMissing;
        private boolean loggedError;

        private MarketplaceIntegration(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        MarketplaceInfo fetch(Player player) {
            if (!ensureHook()) {
                return MarketplaceInfo.unavailable();
            }
            try {
                Object manager = getMarketplaceManagerMethod.invoke(marketplacePlugin);
                if (manager == null) {
                    return MarketplaceInfo.unavailable();
                }
                if (getListingsMethod == null) {
                    getListingsMethod = manager.getClass().getMethod("getListings");
                }
                Object listingsObj = getListingsMethod.invoke(manager);
                if (!(listingsObj instanceof List<?> listings)) {
                    return MarketplaceInfo.unavailable();
                }
                if (shopItemGetSellerMethod == null && !listings.isEmpty()) {
                    Object first = listings.get(0);
                    shopItemGetSellerMethod = first.getClass().getMethod("getSeller");
                }
                int total = listings.size();
                int playerListings = 0;
                if (shopItemGetSellerMethod != null) {
                    for (Object listing : listings) {
                        Object sellerObj = shopItemGetSellerMethod.invoke(listing);
                        if (sellerObj instanceof UUID uuid && uuid.equals(player.getUniqueId())) {
                            playerListings++;
                        }
                    }
                }
                return new MarketplaceInfo(true, playerListings, total);
            } catch (Exception ex) {
                if (!loggedError) {
                    plugin.getLogger().warning("PlayerInfo: Failed to read Marketplace data: " + ex.getMessage());
                    loggedError = true;
                }
                return MarketplaceInfo.unavailable();
            }
        }

        private boolean ensureHook() {
            if (marketplacePlugin != null && marketplacePlugin.isEnabled()) {
                return true;
            }
            Plugin detected = Bukkit.getPluginManager().getPlugin("Marketplace");
            if (detected == null || !detected.isEnabled()) {
                if (!loggedMissing) {
                    plugin.getLogger().info("PlayerInfo: Marketplace plugin not loaded - marketplace section hidden.");
                    loggedMissing = true;
                }
                marketplacePlugin = null;
                return false;
            }
            try {
                Class<?> pluginClass = Class.forName("com.webx.marketplace.MarketplacePlugin");
                if (!pluginClass.isInstance(detected)) {
                    plugin.getLogger().warning("PlayerInfo: Marketplace plugin class mismatch, skipping integration.");
                    marketplacePlugin = null;
                    return false;
                }
                getMarketplaceManagerMethod = pluginClass.getMethod("getMarketplaceManager");
                marketplacePlugin = detected;
                loggedMissing = false;
                loggedError = false;
                plugin.getLogger().info("PlayerInfo: Marketplace integration enabled.");
                return true;
            } catch (Exception ex) {
                plugin.getLogger().warning("PlayerInfo: Unable to hook Marketplace plugin: " + ex.getMessage());
                marketplacePlugin = null;
                return false;
            }
        }
    }

    // ===== FEED =====
    private static final class FeedIntegration {
        private final JavaPlugin plugin;
        private Plugin feedPlugin;
        private Method getFeedManagerMethod;
        private Field hungerRestoreField;
        private boolean loggedMissing;
        private boolean loggedError;

        private FeedIntegration(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        FeedInfo fetch(Player player) {
            int hunger = player.getFoodLevel();
            int saturation = (int) player.getSaturation();
            int restore = 0;

            if (ensureHook()) {
                try {
                    Object manager = getFeedManagerMethod.invoke(feedPlugin);
                    if (manager != null) {
                        if (hungerRestoreField == null) {
                            hungerRestoreField = manager.getClass().getDeclaredField("hungerRestore");
                            hungerRestoreField.setAccessible(true);
                        }
                        restore = hungerRestoreField.getInt(manager);
                    }
                } catch (Exception ex) {
                    if (!loggedError) {
                        plugin.getLogger().warning("PlayerInfo: Failed to read Feed config: " + ex.getMessage());
                        loggedError = true;
                    }
                }
            }

            boolean available = restore > 0;
            return new FeedInfo(available, hunger, saturation, restore);
        }

        private boolean ensureHook() {
            if (feedPlugin != null && feedPlugin.isEnabled()) {
                return true;
            }
            Plugin detected = Bukkit.getPluginManager().getPlugin("Feed");
            if (detected == null || !detected.isEnabled()) {
                if (!loggedMissing) {
                    plugin.getLogger().info("PlayerInfo: Feed plugin not loaded - feed section shows vanilla hunger only.");
                    loggedMissing = true;
                }
                feedPlugin = null;
                return false;
            }
            try {
                Class<?> pluginClass = Class.forName("com.webx.feed.FeedPlugin");
                if (!pluginClass.isInstance(detected)) {
                    plugin.getLogger().warning("PlayerInfo: Feed plugin class mismatch, skipping integration.");
                    feedPlugin = null;
                    return false;
                }
                getFeedManagerMethod = pluginClass.getMethod("getFeedManager");
                feedPlugin = detected;
                loggedMissing = false;
                loggedError = false;
                plugin.getLogger().info("PlayerInfo: Feed integration enabled.");
                return true;
            } catch (Exception ex) {
                plugin.getLogger().warning("PlayerInfo: Unable to hook Feed plugin: " + ex.getMessage());
                feedPlugin = null;
                return false;
            }
        }
    }

    // ===== Shared data cache =====
    private static final class SharedDataCache {
        private final JavaPlugin plugin;
        private final Path databasePath;
        private JsonObject root;
        private long lastModified = -1L;
        private boolean loggedMissing;

        private SharedDataCache(JavaPlugin plugin) {
            this.plugin = plugin;
            this.databasePath = resolvePath(plugin);
        }

        void refresh() {
            try {
                if (!Files.exists(databasePath)) {
                    root = null;
                    if (!loggedMissing) {
                        plugin.getLogger().fine("PlayerInfo: Shared database not found at " + databasePath);
                        loggedMissing = true;
                    }
                    lastModified = -1L;
                    return;
                }
                FileTime modifiedTime = Files.getLastModifiedTime(databasePath);
                long modified = modifiedTime.toMillis();
                if (modified == lastModified && root != null) {
                    return;
                }
                try (Reader reader = Files.newBufferedReader(databasePath, StandardCharsets.UTF_8)) {
                    root = JsonParser.parseReader(reader).getAsJsonObject();
                    lastModified = modified;
                    loggedMissing = false;
                }
            } catch (IOException | IllegalStateException ex) {
                plugin.getLogger().warning("PlayerInfo: Unable to read shared database: " + ex.getMessage());
                root = null;
                lastModified = -1L;
            }
        }

        JsonObject getPlayerChild(UUID uuid, String childKey) {
            JsonObject player = getPlayerSection(uuid);
            if (player == null) {
                return null;
            }
            if (!player.has(childKey) || !player.get(childKey).isJsonObject()) {
                return null;
            }
            return player.getAsJsonObject(childKey);
        }

        JsonObject getPluginSection(String pluginName) {
            if (root == null || !root.has("plugins")) {
                return null;
            }
            JsonElement pluginsNode = root.get("plugins");
            if (!pluginsNode.isJsonObject()) {
                return null;
            }
            JsonElement pluginNode = pluginsNode.getAsJsonObject().get(pluginName);
            if (pluginNode == null || !pluginNode.isJsonObject()) {
                return null;
            }
            return pluginNode.getAsJsonObject();
        }

        private JsonObject getPlayerSection(UUID uuid) {
            if (root == null || !root.has("players")) {
                return null;
            }
            JsonElement playersNode = root.get("players");
            if (!playersNode.isJsonObject()) {
                return null;
            }
            JsonElement playerNode = playersNode.getAsJsonObject().get(uuid.toString());
            if (playerNode == null || !playerNode.isJsonObject()) {
                return null;
            }
            return playerNode.getAsJsonObject();
        }

        private static Path resolvePath(JavaPlugin plugin) {
            String override = System.getProperty("lxxv.cache.dir");
            if (override != null && !override.isBlank()) {
                return Paths.get(override).resolve("lxxv_plugins_server.json");
            }
            Path pluginDir = plugin.getDataFolder().toPath();
            Path serverRoot = pluginDir.getParent() != null ? pluginDir.getParent().getParent() : pluginDir.getParent();
            if (serverRoot == null) {
                serverRoot = pluginDir;
            }
            return serverRoot.resolve("cache").resolve("lxxv_plugins_server.json");
        }
    }

    private static String defaultString(String input) {
        return input == null ? "" : ChatColor.stripColor(input);
    }
}
