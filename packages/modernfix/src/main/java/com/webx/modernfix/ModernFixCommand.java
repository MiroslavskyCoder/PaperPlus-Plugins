package com.webx.modernfix;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Enhanced command handler for ModernFix
 * Provides detailed server statistics and optimization controls
 */
public class ModernFixCommand implements CommandExecutor {
    private final ModernFixPlugin plugin;
    private final OptimizationConfig config;

    public ModernFixCommand(ModernFixPlugin plugin, OptimizationConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showUsage(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "stats":
            case "status":
            case "info":
                showStats(sender);
                return true;
            case "reload":
                return handleReload(sender);
            case "gc":
                return handleGC(sender);
            case "optimize":
                return handleOptimize(sender);
            case "metrics":
                showMetrics(sender);
                return true;
            default:
                sender.sendMessage("§c§lНеизвестная команда: §f" + subcommand);
                showUsage(sender);
                return false;
        }
    }

    private void showUsage(sender) {
        sender.sendMessage("§6╔═══════════════════════════════════╗");
        sender.sendMessage("§6║   §eModernFix §7Commands          §6║");
        sender.sendMessage("§6╠═══════════════════════════════════╣");
        sender.sendMessage("§6║ §7/modernfix stats §8- Статистика §6║");
        sender.sendMessage("§6║ §7/modernfix metrics §8- Метрики  §6║");
        sender.sendMessage("§6║ §7/modernfix reload §8- Reload    §6║");
        sender.sendMessage("§6║ §7/modernfix gc §8- Очистка ОЗУ   §6║");
        sender.sendMessage("§6║ §7/modernfix optimize §8- Оптим.  §6║");
        sender.sendMessage("§6╚═══════════════════════════════════╝");
    }

    private void showStats(CommandSender sender) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double tps = Bukkit.getTPS()[0];
        
        sender.sendMessage("§6╔═══════════════════════════════════╗");
        sender.sendMessage("§6║   §eModernFix §7Статистика        §6║");
        sender.sendMessage("§6╠═══════════════════════════════════╣");
        
        // Performance metrics
        String tpsColor = tps >= 19.5 ? "§a" : tps >= 18.0 ? "§e" : "§c";
        sender.sendMessage(String.format("§6║ §7TPS: " + tpsColor + "%-27.2f§6║", tps));
        sender.sendMessage(String.format("§6║ §7Память: §f%.0f§7/§f%.0f §7МБ §8(§f%.1f%%§8)  §6║",
                usedMemory / 1024.0 / 1024.0,
                maxMemory / 1024.0 / 1024.0,
                (double) usedMemory / maxMemory * 100));
        
        // Entity count
        int totalEntities = 0;
        int totalChunks = 0;
        for (World world : Bukkit.getWorlds()) {
            totalEntities += world.getEntities().size();
            totalChunks += world.getLoadedChunks().length;
        }
        sender.sendMessage(String.format("§6║ §7Сущностей: §f%-21d§6║", totalEntities));
        sender.sendMessage(String.format("§6║ §7Чанков: §f%-24d§6║", totalChunks));
        
        // Optimization statistics
        sender.sendMessage("§6╠═══════════════════════════════════╣");
        sender.sendMessage(String.format("§6║ §7Всего оптимизаций: §f%-14d§6║", plugin.getTotalOptimizations()));
        sender.sendMessage(String.format("§6║ §7Удалено сущностей: §f%-14d§6║", plugin.getTotalEntitiesRemoved()));
        sender.sendMessage(String.format("§6║ §7Память освобождено: §f%-10d §7МБ§6║", 
                plugin.getTotalMemoryFreed() / 1024 / 1024));
        sender.sendMessage(String.format("§6║ §7Кэшировано чанков: §f%-15d§6║", plugin.getCachedChunksCount()));
        
        sender.sendMessage("§6╚═══════════════════════════════════╝");
    }

    private void showMetrics(CommandSender sender) {
        Map<String, ModernFixPlugin.PerformanceMetric> metrics = plugin.getPerformanceMetrics();
        
        sender.sendMessage("§6╔═════════════════════════════════════╗");
        sender.sendMessage("§6║   §ePerformance Metrics              §6║");
        sender.sendMessage("§6╠═════════════════════════════════════╣");
        
        if (metrics.isEmpty()) {
            sender.sendMessage("§6║ §7No data yet - wait for tasks      §6║");
        } else {
            for (Map.Entry<String, ModernFixPlugin.PerformanceMetric> entry : metrics.entrySet()) {
                ModernFixPlugin.PerformanceMetric metric = entry.getValue();
                String name = formatMetricName(entry.getKey());
                
                sender.sendMessage(String.format("§6║ §e%s:", name));
                sender.sendMessage(String.format("§6║  §7Executions: §f%d", metric.getExecutionCount()));
                sender.sendMessage(String.format("§6║  §7Avg: §f%.2fms §8Min: §f%dms §8Max: §f%dms",
                        metric.getAverageDuration(), metric.getMinDuration(), metric.getMaxDuration()));
                sender.sendMessage(String.format("§6║  §7Total items: §f%d §8(avg: §f%.1f§8)",
                        metric.getTotalItems(), metric.getAverageItems()));
                sender.sendMessage("§6║                                     §6║");
            }
        }
        
        sender.sendMessage("§6╚═════════════════════════════════════╝");
    }

    private String formatMetricName(String key) {
        switch (key) {
            case "entity_cleanup": return "Entity Cleanup";
            case "chunk_optimization": return "Chunk Cache";
            case "memory_optimization": return "Memory Optimization";
            default: return key.replace("_", " ");
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("modernfix.reload")) {
            sender.sendMessage("§c§lНет прав! Требуется §fmodernfix.reload");
            return true;
        }

        long startTime = System.currentTimeMillis();
        plugin.reloadPluginConfig();
        long duration = System.currentTimeMillis() - startTime;
        
        sender.sendMessage("§a§l✓ §aКонфигурация перезагружена за " + duration + " мс");
        sender.sendMessage("§7Оптимизации перезапущены с новыми настройками");
        return true;
    }

    private boolean handleGC(CommandSender sender) {
        if (!sender.hasPermission("modernfix.gc")) {
            sender.sendMessage("§c§lНет прав! Требуется §fmodernfix.gc");
            return true;
        }

        Runtime runtime = Runtime.getRuntime();
        long beforeGC = runtime.totalMemory() - runtime.freeMemory();
        
        sender.sendMessage("§e⚡ Запуск сборщика мусора...");
        
        long startTime = System.currentTimeMillis();
        System.gc();
        try {
            Thread.sleep(100); // Give GC time
        } catch (InterruptedException ignored) {}
        long duration = System.currentTimeMillis() - startTime;
        
        long afterGC = runtime.totalMemory() - runtime.freeMemory();
        long freed = Math.max(0, beforeGC - afterGC) / 1024 / 1024;
        
        sender.sendMessage(String.format("§a§l✓ §aСборка мусора завершена за %d мс", duration));
        sender.sendMessage(String.format("§7Освобождено: §f%d МБ", freed));
        sender.sendMessage(String.format("§7Текущее использование: §f%.1f%%", 
                (double) afterGC / runtime.maxMemory() * 100));
        
        return true;
    }

    private boolean handleOptimize(CommandSender sender) {
        if (!sender.hasPermission("modernfix.optimize")) {
            sender.sendMessage("§c§lНет прав! Требуется §fmodernfix.optimize");
            return true;
        }

        sender.sendMessage("§e⚡ Запуск принудительной оптимизации...");
        
        long startTime = System.currentTimeMillis();
        plugin.forceOptimize();
        long duration = System.currentTimeMillis() - startTime;
        
        sender.sendMessage(String.format("§a§l✓ §aОптимизация завершена за %d мс", duration));
        sender.sendMessage("§7Используйте §f/modernfix stats §7для просмотра результатов");
        
        return true;
    }
}
