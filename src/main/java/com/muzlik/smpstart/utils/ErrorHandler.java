package com.muzlik.smpstart.utils;

import com.muzlik.smpstart.SMPStartPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Centralized error handling and logging utility
 */
public class ErrorHandler {
    
    private final SMPStartPlugin plugin;
    
    public ErrorHandler(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle an exception with context information
     * @param context description of what was happening when the error occurred
     * @param exception the exception that occurred
     */
    public void handleException(String context, Exception exception) {
        plugin.getLogger().severe("Error in " + context + ": " + exception.getMessage());
        
        if (plugin.getConfig().getBoolean("debug", false)) {
            exception.printStackTrace();
        }
    }
    
    /**
     * Log an informational message with context
     * @param context the context or component
     * @param message the message to log
     */
    public void logInfo(String context, String message) {
        plugin.getLogger().info("[" + context + "] " + message);
    }
    
    /**
     * Log a warning message with context
     * @param context the context or component
     * @param message the warning message
     */
    public void logWarning(String context, String message) {
        plugin.getLogger().warning("[" + context + "] " + message);
    }
    
    /**
     * Send an error message to a command sender
     * @param sender the command sender
     * @param message the error message
     */
    public void sendError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + "[MSS Error] " + message);
    }
    
    /**
     * Send a success message to a command sender
     * @param sender the command sender
     * @param message the success message
     */
    public void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + "[MSS] " + message);
    }
    
    /**
     * Send an info message to a command sender
     * @param sender the command sender
     * @param message the info message
     */
    public void sendInfo(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.YELLOW + "[MSS] " + message);
    }
    
    /**
     * Validate that an object is not null, throwing an exception if it is
     * @param object the object to check
     * @param name the name of the object for error messages
     * @throws IllegalStateException if the object is null
     */
    public void requireNonNull(Object object, String name) {
        if (object == null) {
            throw new IllegalStateException(name + " is null - initialization failed");
        }
    }
    
    /**
     * Validate a configuration value is within acceptable bounds
     * @param value the value to check
     * @param min minimum acceptable value
     * @param max maximum acceptable value
     * @param name name of the configuration value
     * @return true if valid, false otherwise
     */
    public boolean validateConfigValue(double value, double min, double max, String name) {
        if (value < min || value > max) {
            logWarning("Config Validation", name + " value " + value + " is outside acceptable range [" + min + ", " + max + "]");
            return false;
        }
        return true;
    }
    
    /**
     * Handle a recoverable error by logging it and attempting to continue
     * @param context description of what was happening
     * @param error the error message
     * @param fallbackAction description of the fallback action taken
     */
    public void handleRecoverableError(String context, String error, String fallbackAction) {
        logWarning(context, error + " - " + fallbackAction);
    }
}