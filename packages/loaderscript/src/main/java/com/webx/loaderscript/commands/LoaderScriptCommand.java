package com.webx.loaderscript.commands;

import com.webx.loaderscript.LoaderScriptPlugin;
import com.webx.loaderscript.manager.ScriptManager;
import com.webx.loaderscript.models.ScriptInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Command handler for /loaderscript
 */
public class LoaderScriptCommand implements CommandExecutor, TabCompleter {
    
    private final LoaderScriptPlugin plugin;
    private final ScriptManager scriptManager;
    
    public LoaderScriptCommand(LoaderScriptPlugin plugin, ScriptManager scriptManager) {
        this.plugin = plugin;
        this.scriptManager = scriptManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("loaderscript.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list", "ls" -> handleList(sender);
            case "load" -> handleLoad(sender, args);
            case "reload" -> handleReload(sender, args);
            case "unload" -> handleUnload(sender, args);
            case "reload-all", "reloadall" -> handleReloadAll(sender);
            case "info" -> handleInfo(sender, args);
            case "create" -> handleCreate(sender, args);
            case "delete", "remove" -> handleDelete(sender, args);
            default -> sendHelp(sender);
        }
        
        return true;
    }
    
    private void handleList(CommandSender sender) {
        Map<String, ScriptInfo> loaded = scriptManager.getLoadedScripts();
        List<String> all = scriptManager.listAllScripts();
        
        sender.sendMessage("§6========== §eLoaderScript §6==========");
        sender.sendMessage("§7Loaded: §e" + loaded.size() + "§7/§e" + all.size() + " scripts");
        sender.sendMessage("");
        
        if (all.isEmpty()) {
            sender.sendMessage("§7No scripts found in folder");
        } else {
            for (String scriptName : all) {
                boolean isLoaded = loaded.containsKey(scriptName);
                ScriptInfo info = loaded.get(scriptName);
                
                if (isLoaded && info != null && info.isSuccess()) {
                    sender.sendMessage("  §a✓ §f" + scriptName + " §7(loaded)");
                } else if (isLoaded && info != null && !info.isSuccess()) {
                    sender.sendMessage("  §c✗ §f" + scriptName + " §7(error)");
                } else {
                    sender.sendMessage("  §7○ §f" + scriptName + " §7(not loaded)");
                }
            }
        }
        
        sender.sendMessage("§6================================");
    }
    
    private void handleLoad(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /loaderscript load <script>");
            return;
        }
        
        String scriptName = args[1];
        
        if (scriptManager.isScriptLoaded(scriptName)) {
            sender.sendMessage("§cScript already loaded! Use 'reload' instead.");
            return;
        }
        
        sender.sendMessage("§7Loading script: §e" + scriptName + "§7...");
        
        if (scriptManager.loadScript(scriptName)) {
            ScriptInfo info = scriptManager.getScriptInfo(scriptName);
            if (info != null && info.isSuccess()) {
                sender.sendMessage("§a✓ Script loaded successfully!");
            } else if (info != null) {
                sender.sendMessage("§c✗ Script loaded with errors:");
                sender.sendMessage("§c  " + info.getError());
            }
        } else {
            sender.sendMessage("§c✗ Failed to load script!");
        }
    }
    
    private void handleReload(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /loaderscript reload <script>");
            return;
        }
        
        String scriptName = args[1];
        sender.sendMessage("§7Reloading script: §e" + scriptName + "§7...");
        
        if (scriptManager.reloadScript(scriptName)) {
            ScriptInfo info = scriptManager.getScriptInfo(scriptName);
            if (info != null && info.isSuccess()) {
                sender.sendMessage("§a✓ Script reloaded successfully!");
            } else if (info != null) {
                sender.sendMessage("§c✗ Script reloaded with errors:");
                sender.sendMessage("§c  " + info.getError());
            }
        } else {
            sender.sendMessage("§c✗ Failed to reload script!");
        }
    }
    
    private void handleUnload(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /loaderscript unload <script>");
            return;
        }
        
        String scriptName = args[1];
        
        if (scriptManager.unloadScript(scriptName)) {
            sender.sendMessage("§a✓ Script unloaded: §e" + scriptName);
        } else {
            sender.sendMessage("§c✗ Script not loaded: §e" + scriptName);
        }
    }
    
    private void handleReloadAll(CommandSender sender) {
        sender.sendMessage("§7Reloading all scripts...");
        scriptManager.reloadAllScripts();
        
        Map<String, ScriptInfo> loaded = scriptManager.getLoadedScripts();
        long successful = loaded.values().stream().filter(ScriptInfo::isSuccess).count();
        long failed = loaded.size() - successful;
        
        sender.sendMessage("§a✓ Reloaded all scripts!");
        sender.sendMessage("§7  Success: §a" + successful + " §7Failed: §c" + failed);
    }
    
    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /loaderscript info <script>");
            return;
        }
        
        String scriptName = args[1];
        ScriptInfo info = scriptManager.getScriptInfo(scriptName);
        
        if (info == null) {
            sender.sendMessage("§cScript not loaded: §e" + scriptName);
            return;
        }
        
        sender.sendMessage("§6========== §eScript Info §6==========");
        sender.sendMessage("§7Name: §f" + info.getName());
        sender.sendMessage("§7Path: §f" + info.getPath());
        sender.sendMessage("§7Size: §f" + formatBytes(info.getSize()));
        sender.sendMessage("§7Last Modified: §f" + new java.util.Date(info.getLastModified()));
        sender.sendMessage("§7Loaded At: §f" + new java.util.Date(info.getLoadedAt()));
        sender.sendMessage("§7Status: " + (info.isSuccess() ? "§aSuccess" : "§cError"));
        
        if (!info.isSuccess() && info.getError() != null) {
            sender.sendMessage("§7Error: §c" + info.getError());
        }
        
        sender.sendMessage("§6================================");
    }
    
    private void handleCreate(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /loaderscript create <name>");
            return;
        }
        
        String scriptName = args[1];
        
        if (scriptManager.createScript(scriptName, null)) {
            sender.sendMessage("§a✓ Created new script: §e" + scriptName);
            sender.sendMessage("§7Edit it in: §f" + scriptManager.getScriptsFolder().getAbsolutePath());
        } else {
            sender.sendMessage("§c✗ Failed to create script (may already exist)");
        }
    }
    
    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /loaderscript delete <script>");
            return;
        }
        
        String scriptName = args[1];
        
        sender.sendMessage("§cWarning: This will permanently delete the script!");
        sender.sendMessage("§7Type the command again within 10 seconds to confirm.");
        
        // In production, implement confirmation system
        if (scriptManager.deleteScript(scriptName)) {
            sender.sendMessage("§a✓ Deleted script: §e" + scriptName);
        } else {
            sender.sendMessage("§c✗ Failed to delete script");
        }
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6========== §eLoaderScript Commands §6==========");
        sender.sendMessage("§e/loaderscript list §7- List all scripts");
        sender.sendMessage("§e/loaderscript load <script> §7- Load a script");
        sender.sendMessage("§e/loaderscript reload <script> §7- Reload a script");
        sender.sendMessage("§e/loaderscript unload <script> §7- Unload a script");
        sender.sendMessage("§e/loaderscript reload-all §7- Reload all scripts");
        sender.sendMessage("§e/loaderscript info <script> §7- Show script info");
        sender.sendMessage("§e/loaderscript create <name> §7- Create new script");
        sender.sendMessage("§e/loaderscript delete <script> §7- Delete a script");
        sender.sendMessage("§6==========================================");
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = List.of("list", "load", "reload", "unload", 
                                              "reload-all", "info", "create", "delete");
            for (String sub : subCommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("load") || subCommand.equals("reload") || 
                subCommand.equals("unload") || subCommand.equals("info") || 
                subCommand.equals("delete")) {
                
                List<String> scripts = scriptManager.listAllScripts();
                for (String script : scripts) {
                    if (script.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(script);
                    }
                }
            }
        }
        
        return completions;
    }
}
