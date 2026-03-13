package com.muzlik.smpstart.reminders;

import org.bukkit.entity.Player;

/**
 * Manages OP player join reminders
 */
public interface ReminderSystem {
    
    /**
     * Start sending join reminders to offline OP players
     */
    void startReminders();
    
    /**
     * Stop all join reminders
     */
    void stopReminders();
    
    /**
     * Send reminder message to offline OP players
     */
    void sendReminderToOfflineOPs();
    
    /**
     * Stop reminders for a specific player (when they join)
     * @param player the player who joined
     */
    void stopReminderForPlayer(Player player);
    
    /**
     * Check if reminders are currently active
     * @return true if reminders are running, false otherwise
     */
    boolean areRemindersActive();
}