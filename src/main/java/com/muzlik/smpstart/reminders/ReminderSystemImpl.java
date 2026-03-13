package com.muzlik.smpstart.reminders;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.state.StateManager.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of ReminderSystem that handles OP player join reminders
 */
public class ReminderSystemImpl implements ReminderSystem, Listener {
    
    private final SMPStartPlugin plugin;
    private BukkitTask reminderTask;
    private boolean remindersActive = false;
    private final Set<UUID> excludedPlayers = new HashSet<>();
    
    public ReminderSystemImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
        // Register this as an event listener for player join events
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public void startReminders() {
        if (!plugin.getConfigManager().areJoinRemindersEnabled()) {
            plugin.getLogger().info("Join reminders are disabled in config");
            return;
        }
        
        if (remindersActive) {
            plugin.getLogger().info("Join reminders are already active");
            return;
        }
        
        remindersActive = true;
        excludedPlayers.clear();
        
        int interval = plugin.getConfigManager().getReminderInterval();
        
        // Start reminder task
        reminderTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (remindersActive) {
                    sendReminderToOfflineOPs();
                }
            }
        }.runTaskTimer(plugin, 0L, interval * 20L); // Convert seconds to ticks
        
        plugin.getLogger().info("Started join reminders with " + interval + " second interval");
    }
    
    @Override
    public void stopReminders() {
        if (!remindersActive) {
            return;
        }
        
        remindersActive = false;
        
        if (reminderTask != null) {
            reminderTask.cancel();
            reminderTask = null;
        }
        
        excludedPlayers.clear();
        plugin.getLogger().info("Stopped join reminders");
    }
    
    @Override
    public void sendReminderToOfflineOPs() {
        if (!remindersActive || !plugin.getConfigManager().areJoinRemindersEnabled()) {
            return;
        }
        
        try {
            Set<OfflinePlayer> offlineOPs = getOfflineOPs();
            
            if (offlineOPs.isEmpty()) {
                plugin.getLogger().info("No offline OP players to remind");
                return;
            }
            
            // Send reminder message to online players about offline OPs
            String message = ChatColor.YELLOW + "[SMP] Waiting for OP players: ";
            StringBuilder opNames = new StringBuilder();
            
            for (OfflinePlayer op : offlineOPs) {
                if (opNames.length() > 0) {
                    opNames.append(", ");
                }
                opNames.append(op.getName());
            }
            
            message += ChatColor.WHITE + opNames.toString();
            
            // Broadcast to all online players
            Bukkit.broadcastMessage(message);
            
            plugin.getLogger().info("Sent reminder about " + offlineOPs.size() + " offline OP players");
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send reminder to offline OPs: " + e.getMessage());
        }
    }
    
    @Override
    public void stopReminderForPlayer(Player player) {
        if (player.isOp()) {
            excludedPlayers.add(player.getUniqueId());
            plugin.getLogger().info("Stopped reminders for OP player: " + player.getName());
        }
    }
    
    @Override
    public boolean areRemindersActive() {
        return remindersActive;
    }
    
    /**
     * Get all offline OP players (excluding those who have joined and been excluded)
     * @return set of offline OP players
     */
    private Set<OfflinePlayer> getOfflineOPs() {
        Set<OfflinePlayer> offlineOPs = new HashSet<>();
        
        // Get all OP players
        for (OfflinePlayer player : Bukkit.getOperators()) {
            // Skip if player is online
            if (player.isOnline()) {
                continue;
            }
            
            // Skip if player has been excluded (joined and then left)
            if (excludedPlayers.contains(player.getUniqueId())) {
                continue;
            }
            
            offlineOPs.add(player);
        }
        
        return offlineOPs;
    }
    
    /**
     * Event handler for player join events
     * @param event the player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Delay processing to ensure player is fully loaded
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // If this is an OP player
            if (player.isOp()) {
                plugin.getLogger().info("OP player " + player.getName() + " joined. Current state: " + plugin.getStateManager().getCurrentState());
                
                // Send welcome message with commands if SMP hasn't started yet
                if (plugin.getStateManager().getCurrentState() == PluginState.IDLE) {
                    plugin.getLogger().info("Sending admin welcome message to " + player.getName());
                    sendAdminWelcomeMessage(player);
                }
                
                // If reminders are active, stop reminders for them
                if (remindersActive) {
                    stopReminderForPlayer(player);
                    
                    // Announce that the OP has joined
                    String message = ChatColor.GREEN + "[SMP] OP player " + ChatColor.WHITE + player.getName() + 
                                   ChatColor.GREEN + " has joined the server!";
                    Bukkit.broadcastMessage(message);
                }
            }
        }, 10L); // Wait 0.5 seconds (10 ticks) for player to fully load
    }
    
    /**
     * Send welcome message to admin players with command information
     * @param player the admin player
     */
    private void sendAdminWelcomeMessage(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "=== SMP Start Plugin - Admin Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/smpstart" + ChatColor.WHITE + " - Start the SMP countdown");
        player.sendMessage(ChatColor.YELLOW + "/smpconfig help" + ChatColor.WHITE + " - Show all configuration options");
        player.sendMessage(ChatColor.YELLOW + "/smpconfig status" + ChatColor.WHITE + " - Show current plugin status");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "Current Border: " + ChatColor.WHITE + 
                          plugin.getConfigManager().getPreStartBorderSize() + " blocks → " + 
                          plugin.getConfigManager().getFinalBorderSize() + " blocks");
        player.sendMessage(ChatColor.GREEN + "Countdown Duration: " + ChatColor.WHITE + 
                          plugin.getConfigManager().getCountdownDuration() + " seconds");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "Use /smpstart when ready to begin the SMP!");
        player.sendMessage("");
    }
    
    /**
     * Cleanup method to cancel any running tasks
     */
    public void cleanup() {
        stopReminders();
    }
}