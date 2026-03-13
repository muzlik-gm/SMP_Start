package com.muzlik.smpstart.commands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.state.StateManager.PluginState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of CommandManager that handles all plugin commands
 */
public class CommandManagerImpl implements CommandManager {
    
    private final SMPStartPlugin plugin;
    
    public CommandManagerImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void registerCommands() {
        // Register command executors
        plugin.getCommand("smpstart").setExecutor(this);
        plugin.getCommand("smpconfig").setExecutor(this);
        
        // Register tab completers
        plugin.getCommand("smpstart").setTabCompleter(this);
        plugin.getCommand("smpconfig").setTabCompleter(this);
        
        plugin.getLogger().info("Registered plugin commands");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("smpstart")) {
            return handleSMPStartCommand(sender, args);
        } else if (command.getName().equalsIgnoreCase("smpconfig")) {
            return handleConfigCommand(sender, args);
        }
        return false;
    }
    
    @Override
    public boolean handleSMPStartCommand(CommandSender sender, String[] args) {
        // Check permissions
        if (!sender.hasPermission("smpstart.use")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        // Check if command can be executed
        if (!plugin.getStateManager().canExecuteStart()) {
            PluginState currentState = plugin.getStateManager().getCurrentState();
            
            if (currentState == PluginState.COUNTDOWN) {
                int remaining = plugin.getStateManager().getRemainingCountdown();
                sender.sendMessage(ChatColor.YELLOW + "SMP countdown is already active! " + 
                                 remaining + " seconds remaining.");
            } else if (currentState == PluginState.COOLDOWN) {
                int remaining = plugin.getStateManager().getRemainingCooldown();
                sender.sendMessage(ChatColor.YELLOW + "SMP start is on cooldown! " + 
                                 remaining + " seconds remaining.");
            }
            return true;
        }
        
        // Start the countdown
        plugin.getStateManager().startCountdown();
        
        String message = ChatColor.GREEN + "SMP countdown started by " + 
                        ChatColor.WHITE + sender.getName() + ChatColor.GREEN + "!";
        plugin.getServer().broadcastMessage(message);
        
        return true;
    }
    
    @Override
    public boolean handleConfigCommand(CommandSender sender, String[] args) {
        // Check permissions
        if (!sender.hasPermission("smpstart.config")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                showHelp(sender);
                break;
                
            case "countdown":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /smpconfig countdown <seconds>");
                    return true;
                }
                try {
                    int duration = Integer.parseInt(args[1]);
                    if (duration <= 0) {
                        sender.sendMessage(ChatColor.RED + "Countdown duration must be positive!");
                        return true;
                    }
                    plugin.getConfigManager().setCountdownDuration(duration);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Countdown duration set to " + duration + " seconds.");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
                }
                break;
                
            case "cooldown":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /smpconfig cooldown <seconds>");
                    return true;
                }
                try {
                    int duration = Integer.parseInt(args[1]);
                    if (duration <= 0) {
                        sender.sendMessage(ChatColor.RED + "Cooldown duration must be positive!");
                        return true;
                    }
                    plugin.getConfigManager().setCooldownDuration(duration);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Cooldown duration set to " + duration + " seconds.");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
                }
                break;
                
            case "preborder":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /smpconfig preborder <size>");
                    return true;
                }
                try {
                    double size = Double.parseDouble(args[1]);
                    if (!plugin.getConfigManager().isValidBorderSize(size)) {
                        sender.sendMessage(ChatColor.RED + "Border size must be positive!");
                        return true;
                    }
                    plugin.getConfigManager().setPreStartBorderSize(size);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Pre-start border size set to " + size + " blocks.");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
                }
                break;
                
            case "finalborder":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /smpconfig finalborder <size>");
                    return true;
                }
                try {
                    double size = Double.parseDouble(args[1]);
                    if (!plugin.getConfigManager().isValidBorderSize(size)) {
                        sender.sendMessage(ChatColor.RED + "Border size must be positive!");
                        return true;
                    }
                    plugin.getConfigManager().setFinalBorderSize(size);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Final border size set to " + size + " blocks.");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
                }
                break;
                
            case "reminders":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /smpconfig reminders <true|false>");
                    return true;
                }
                boolean enabled = Boolean.parseBoolean(args[1]);
                plugin.getConfigManager().setJoinRemindersEnabled(enabled);
                plugin.getConfigManager().saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Join reminders " + 
                                 (enabled ? "enabled" : "disabled") + ".");
                break;
                
            case "status":
                showStatus(sender);
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + subCommand);
                showHelp(sender);
                break;
        }
        
        return true;
    }
    
    @Override
    public void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== SMP Start Plugin Help ===");
        sender.sendMessage(ChatColor.YELLOW + "/smpstart" + ChatColor.WHITE + " - Start the SMP countdown");
        sender.sendMessage(ChatColor.YELLOW + "/smpconfig help" + ChatColor.WHITE + " - Show this help");
        sender.sendMessage(ChatColor.YELLOW + "/smpconfig status" + ChatColor.WHITE + " - Show current configuration");
        sender.sendMessage(ChatColor.YELLOW + "/smpconfig countdown <seconds>" + ChatColor.WHITE + " - Set countdown duration");
        sender.sendMessage(ChatColor.YELLOW + "/smpconfig cooldown <seconds>" + ChatColor.WHITE + " - Set cooldown duration");
        sender.sendMessage(ChatColor.YELLOW + "/smpconfig preborder <size>" + ChatColor.WHITE + " - Set pre-start border size");
        sender.sendMessage(ChatColor.YELLOW + "/smpconfig finalborder <size>" + ChatColor.WHITE + " - Set final border size");
        sender.sendMessage(ChatColor.YELLOW + "/smpconfig reminders <true|false>" + ChatColor.WHITE + " - Enable/disable join reminders");
    }
    
    private void showStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== SMP Start Plugin Status ===");
        
        // Current state
        PluginState state = plugin.getStateManager().getCurrentState();
        sender.sendMessage(ChatColor.YELLOW + "Current State: " + ChatColor.WHITE + state.name());
        
        if (state == PluginState.COUNTDOWN) {
            int remaining = plugin.getStateManager().getRemainingCountdown();
            sender.sendMessage(ChatColor.YELLOW + "Countdown Remaining: " + ChatColor.WHITE + remaining + " seconds");
        } else if (state == PluginState.COOLDOWN) {
            int remaining = plugin.getStateManager().getRemainingCooldown();
            sender.sendMessage(ChatColor.YELLOW + "Cooldown Remaining: " + ChatColor.WHITE + remaining + " seconds");
        }
        
        // Configuration
        sender.sendMessage(ChatColor.YELLOW + "Countdown Duration: " + ChatColor.WHITE + 
                          plugin.getConfigManager().getCountdownDuration() + " seconds");
        sender.sendMessage(ChatColor.YELLOW + "Cooldown Duration: " + ChatColor.WHITE + 
                          plugin.getConfigManager().getCooldownDuration() + " seconds");
        sender.sendMessage(ChatColor.YELLOW + "Pre-start Border: " + ChatColor.WHITE + 
                          plugin.getConfigManager().getPreStartBorderSize() + " blocks");
        sender.sendMessage(ChatColor.YELLOW + "Final Border: " + ChatColor.WHITE + 
                          plugin.getConfigManager().getFinalBorderSize() + " blocks");
        sender.sendMessage(ChatColor.YELLOW + "Join Reminders: " + ChatColor.WHITE + 
                          (plugin.getConfigManager().areJoinRemindersEnabled() ? "Enabled" : "Disabled"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (command.getName().equalsIgnoreCase("smpconfig")) {
            if (args.length == 1) {
                // First argument - subcommands
                List<String> subCommands = Arrays.asList("help", "status", "countdown", "cooldown", 
                                                       "preborder", "finalborder", "reminders");
                for (String subCmd : subCommands) {
                    if (subCmd.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(subCmd);
                    }
                }
            } else if (args.length == 2) {
                // Second argument - values based on subcommand
                String subCommand = args[0].toLowerCase();
                if (subCommand.equals("reminders")) {
                    completions.add("true");
                    completions.add("false");
                }
            }
        }
        
        return completions;
    }
}