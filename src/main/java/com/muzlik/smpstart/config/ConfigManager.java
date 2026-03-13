package com.muzlik.smpstart.config;

/**
 * Handles all configurable values and persistence
 */
public interface ConfigManager {
    
    /**
     * Get the countdown duration in seconds
     * @return countdown duration
     */
    int getCountdownDuration();
    
    /**
     * Set the countdown duration in seconds
     * @param duration countdown duration
     */
    void setCountdownDuration(int duration);
    
    /**
     * Get the cooldown duration in seconds
     * @return cooldown duration
     */
    int getCooldownDuration();
    
    /**
     * Set the cooldown duration in seconds
     * @param duration cooldown duration
     */
    void setCooldownDuration(int duration);
    
    /**
     * Get the pre-start world border size
     * @return border size in blocks
     */
    double getPreStartBorderSize();
    
    /**
     * Set the pre-start world border size
     * @param size border size in blocks
     */
    void setPreStartBorderSize(double size);
    
    /**
     * Get the final world border size
     * @return border size in blocks
     */
    double getFinalBorderSize();
    
    /**
     * Set the final world border size
     * @param size border size in blocks
     */
    void setFinalBorderSize(double size);
    
    /**
     * Check if join reminders are enabled
     * @return true if enabled, false otherwise
     */
    boolean areJoinRemindersEnabled();
    
    /**
     * Enable or disable join reminders
     * @param enabled true to enable, false to disable
     */
    void setJoinRemindersEnabled(boolean enabled);
    
    /**
     * Get the reminder interval in seconds
     * @return reminder interval
     */
    int getReminderInterval();
    
    /**
     * Set the reminder interval in seconds
     * @param interval reminder interval
     */
    void setReminderInterval(int interval);
    
    /**
     * Save configuration to file
     */
    void saveConfig();
    
    /**
     * Reload configuration from file
     */
    void reloadConfig();
    
    /**
     * Validate a border size value
     * @param size the size to validate
     * @return true if valid (positive), false otherwise
     */
    boolean isValidBorderSize(double size);
    
    /**
     * Get the PvP protection duration in minutes
     * @return protection duration
     */
    int getPvpProtectionDuration();
    
    /**
     * Set the PvP protection duration in minutes
     * @param duration protection duration (0 to disable)
     */
    void setPvpProtectionDuration(int duration);
}