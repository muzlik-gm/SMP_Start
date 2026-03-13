package com.muzlik.smpstart.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/**
 * Manages all plugin commands
 */
public interface CommandManager extends CommandExecutor, TabCompleter {
    
    /**
     * Register all plugin commands
     */
    void registerCommands();
    
    /**
     * Handle the /smpstart command
     * @param sender command sender
     * @param args command arguments
     * @return true if command was handled
     */
    boolean handleSMPStartCommand(org.bukkit.command.CommandSender sender, String[] args);
    
    /**
     * Handle configuration commands
     * @param sender command sender
     * @param args command arguments
     * @return true if command was handled
     */
    boolean handleConfigCommand(org.bukkit.command.CommandSender sender, String[] args);
    
    /**
     * Show help information to the sender
     * @param sender command sender
     */
    void showHelp(org.bukkit.command.CommandSender sender);
}